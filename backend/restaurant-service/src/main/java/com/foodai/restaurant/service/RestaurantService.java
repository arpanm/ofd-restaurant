package com.foodai.restaurant.service;

import com.foodai.restaurant.domain.model.Restaurant;
import com.foodai.restaurant.domain.model.RestaurantOutlet;
import com.foodai.restaurant.domain.model.RestaurantStatus;
import com.foodai.restaurant.domain.repository.RestaurantRepository;
import com.foodai.restaurant.domain.repository.RestaurantSearchRepository;
import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.dto.request.*;
import com.foodai.restaurant.dto.response.OutletResponse;
import com.foodai.restaurant.dto.response.OutletSearchResponse;
import com.foodai.restaurant.dto.response.RestaurantResponse;
import com.foodai.restaurant.dto.response.RestaurantSearchResponse;
import com.foodai.restaurant.client.UserServiceClient;
import com.foodai.restaurant.exception.OutletNotFoundException;
import com.foodai.restaurant.exception.RestaurantAlreadyExistsException;
import com.foodai.restaurant.exception.RestaurantNotFoundException;
import com.foodai.restaurant.mapper.RestaurantMapper;
import com.foodai.restaurant.mapper.SearchMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for Restaurant operations.
 * Handles CRUD operations, business logic, and event publishing.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    private final RestaurantSearchRepository restaurantSearchRepository;
    private final RestaurantMapper restaurantMapper;
    private final SearchMapper searchMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final NotificationService notificationService;
    
    /**
     * Create a new restaurant brand.
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        log.info("Creating new restaurant: {}", request.getName());
        
        // Check if restaurant with same name already exists
        restaurantRepository.findByNameIgnoreCase(request.getName())
            .ifPresent(existing -> {
                throw new RestaurantAlreadyExistsException(request.getName());
            });
        
        // Map to entity
        Restaurant restaurant = restaurantMapper.toEntity(request);
        restaurant.setOutlets(new ArrayList<>());
        
        // Validate ownership percentages sum to 100
        validateOwnershipPercentages(restaurant);
        
        // Save restaurant
        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Restaurant created successfully with ID: {}", saved.getId());
        
        // Publish event
        publishRestaurantEvent("restaurant.registered", saved);
        
        return restaurantMapper.toResponse(saved);
    }

    /**
     * Onboarding workflow: submit all steps and create restaurant + first outlet.
     * Uses default contract and outlet config; sets legal and bank fields on the aggregate.
     */
    @Transactional
    @CacheEvict(value = "restaurants", allEntries = true)
    public RestaurantResponse submitOnboarding(SubmitOnboardingRequest req) {
        log.info("Submitting onboarding for restaurant: {}", req.getName());

        restaurantRepository.findByNameIgnoreCase(req.getName())
            .ifPresent(existing -> {
                throw new RestaurantAlreadyExistsException(req.getName());
            });

        CreateRestaurantRequest createReq = buildCreateRestaurantRequestFromOnboarding(req);
        RestaurantResponse created = createRestaurant(createReq);

        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(created.getId())
            .orElseThrow(() -> new RestaurantNotFoundException(created.getId(), true));

        restaurant.setFssaiLicenseNumber(req.getFssaiNumber());
        restaurant.setGstNumber(req.getGstNumber());
        restaurant.setPanNumber(req.getPanNumber());
        restaurant.setBankAccount(com.foodai.restaurant.domain.model.BankAccountVO.builder()
            .bankName(req.getBankName())
            .accountNumber(req.getAccountNumber())
            .ifscCode(req.getIfscCode())
            .accountHolderName(req.getAccountHolderName())
            .cancelledChequeDocumentUrl(req.getCancelledChequeDocumentUrl())
            .build());
        restaurant.setUpdatedBy(req.getCreatedBy());
        restaurant.setUpdatedAt(Instant.now());
        restaurantRepository.save(restaurant);

        CreateOutletRequest outletReq = buildFirstOutletRequestFromOnboarding(req);
        addOutlet(created.getId(), outletReq);

        return getRestaurantById(created.getId());
    }

    /**
     * Post-submit: create owner user for login and send verification email/SMS.
     * Called after onboarding is submitted (from draft or direct). Does not fail the flow if user/notification calls fail.
     */
    public void afterOnboardingSubmitted(String restaurantId, SubmitOnboardingRequest req) {
        try {
            userServiceClient.createOwnerUser(req.getEmail(), req.getPhone(), req.getOwnerName());
        } catch (Exception e) {
            log.warn("Could not create owner user for restaurant {}: {}", restaurantId, e.getMessage());
        }
        try {
            notificationService.sendVerificationEmail(req.getEmail(), req.getName());
            notificationService.sendVerificationSms(req.getPhone(), req.getName());
        } catch (Exception e) {
            log.warn("Could not send verification for restaurant {}: {}", restaurantId, e.getMessage());
        }
    }

    private CreateRestaurantRequest buildCreateRestaurantRequestFromOnboarding(SubmitOnboardingRequest req) {
        List<DocumentDTO> docs = new ArrayList<>();
        docs.add(DocumentDTO.builder().type(DocumentType.FSSAI).url(req.getFssaiDocumentUrl()).build());
        if (req.getGstDocumentUrl() != null && !req.getGstDocumentUrl().isBlank()) {
            docs.add(DocumentDTO.builder().type(DocumentType.GST).url(req.getGstDocumentUrl()).build());
        }
        if (req.getPanDocumentUrl() != null && !req.getPanDocumentUrl().isBlank()) {
            docs.add(DocumentDTO.builder().type(DocumentType.PAN).url(req.getPanDocumentUrl()).build());
        }
        docs.add(DocumentDTO.builder().type(DocumentType.BANK_PROOF).url(req.getCancelledChequeDocumentUrl()).build());

        ContractDTO contract = ContractDTO.builder()
            .signed(Boolean.TRUE)
            .signedBy(req.getContractSignedBy())
            .signedAt(req.getContractSignedAt() != null ? req.getContractSignedAt() : Instant.now())
            .platformFee(PlatformFeeDTO.builder().feeType(FeeType.PERCENTAGE).percentageRate(15.0).fixedAmountPerOrder(0.0).minFeePerOrder(0.0).maxFeePerOrder(0.0).build())
            .deliveryFee(DeliveryFeeDTO.builder().feeType(FeeType.FIXED).payor(DeliveryFeePayor.CUSTOMER).fixedAmountPerOrder(20.0).restaurantSharePercentage(0.0).customerSharePercentage(100.0).minFeePerOrder(0.0).maxFeePerOrder(0.0).build())
            .paymentGatewayFee(PaymentGatewayFeeDTO.builder().feeType(FeeType.PERCENTAGE).payor(PaymentFeePayor.RESTAURANT).percentageRate(2.0).fixedAmount(0.0).minFee(0.0).maxFee(0.0).build())
            .build();

        return CreateRestaurantRequest.builder()
            .name(req.getName())
            .description(req.getDescription())
            .cuisineTypes(req.getCuisineTypes())
            .owners(List.of(OwnerDTO.builder()
                .ownerName(req.getOwnerName())
                .ownerPhone(req.getOwnerPhone())
                .ownerEmail(req.getOwnerEmail() != null ? req.getOwnerEmail() : req.getEmail())
                .ownershipPercentage(100.0)
                .role(OwnerRole.PRIMARY_OWNER)
                .isPrimaryContact(true)
                .build()))
            .contacts(List.of(ContactDTO.builder()
                .contactType(ContactType.PRIMARY)
                .name(req.getOwnerName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .isActive(true)
                .build()))
            .documents(docs)
            .contract(contract)
            .createdBy(req.getCreatedBy())
            .build();
    }

    private CreateOutletRequest buildFirstOutletRequestFromOnboarding(SubmitOnboardingRequest req) {
        List<OperatingHoursDTO> hours = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            hours.add(OperatingHoursDTO.builder()
                .dayOfWeek(day)
                .openTime(LocalTime.of(10, 0))
                .closeTime(LocalTime.of(22, 0))
                .isClosed(false)
                .build());
        }
        return CreateOutletRequest.builder()
            .outletName(req.getName() + " - Main")
            .outletCode("MAIN")
            .address(AddressDTO.builder()
                .street(req.getAddress())
                .city(req.getCity())
                .state(req.getState())
                .pincode(req.getPincode())
                .latitude(0.0)
                .longitude(0.0)
                .build())
            .contacts(List.of(ContactDTO.builder()
                .contactType(ContactType.PRIMARY)
                .name(req.getOwnerName())
                .phone(req.getPhone())
                .email(req.getEmail())
                .isActive(true)
                .build()))
            .operatingHours(hours)
            .serviceabilityConfig(ServiceabilityConfigDTO.builder()
                .serviceablePincodes(List.of(req.getPincode()))
                .maxDeliveryRadius(5.0)
                .useRadiusBased(true)
                .maxOrdersPerHour(50)
                .build())
            .tatConfig(TATConfigDTO.builder()
                .basePrepTimeMinutes(30)
                .tatPerOrderInQueue(5)
                .peakHourExtraMinutes(10)
                .maxTATMinutes(60)
                .bufferTimeMinutes(5)
                .considerRiderAvailability(false)
                .considerBatching(false)
                .considerDistance(false)
                .build())
            .minimumOrderValue(0.0)
            .deliveryFee(0.0)
            .selfDelivery(false)
            .createdBy(req.getCreatedBy())
            .build();
    }
    
    /**
     * Get restaurant by ID.
     */
    @Cacheable(value = "restaurants", key = "#id")
    public RestaurantResponse getRestaurantById(String id) {
        log.debug("Fetching restaurant with ID: {}", id);
        
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new RestaurantNotFoundException(id, true));
        
        return restaurantMapper.toResponse(restaurant);
    }
    
    /**
     * Get all restaurants with pagination.
     */
    public Page<RestaurantResponse> getAllRestaurants(Pageable pageable) {
        log.debug("Fetching all restaurants, page: {}", pageable.getPageNumber());
        
        Page<Restaurant> restaurants = restaurantRepository.findByDeletedFalse(pageable);
        return restaurants.map(restaurantMapper::toResponse);
    }
    
    /**
     * Search restaurants by name or cuisine.
     */
    public Page<RestaurantResponse> searchRestaurants(String searchTerm, Pageable pageable) {
        log.debug("Searching restaurants with term: {}", searchTerm);
        
        Page<Restaurant> restaurants = restaurantRepository.searchByNameOrCuisine(searchTerm, pageable);
        return restaurants.map(restaurantMapper::toResponse);
    }
    
    /**
     * Get restaurants by owner ID.
     */
    public List<RestaurantResponse> getRestaurantsByOwner(String ownerId) {
        log.debug("Fetching restaurants for owner: {}", ownerId);
        
        List<Restaurant> restaurants = restaurantRepository.findByOwnerId(ownerId);
        return restaurantMapper.toResponseList(restaurants);
    }
    
    /**
     * Update restaurant.
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#id")
    public RestaurantResponse updateRestaurant(String id, CreateRestaurantRequest request) {
        log.info("Updating restaurant with ID: {}", id);
        
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new RestaurantNotFoundException(id, true));
        
        // Update fields
        restaurantMapper.updateEntityFromRequest(restaurant, request);
        restaurant.setUpdatedBy(request.getCreatedBy());
        restaurant.setUpdatedAt(Instant.now());
        
        // Save
        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Restaurant updated successfully: {}", id);
        
        // Publish event
        publishRestaurantEvent("restaurant.updated", updated);
        
        return restaurantMapper.toResponse(updated);
    }
    
    /**
     * Soft delete restaurant.
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#id")
    public void deleteRestaurant(String id, String deletedBy) {
        log.info("Deleting restaurant with ID: {}", id);
        
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new RestaurantNotFoundException(id, true));
        
        restaurant.setDeleted(true);
        restaurant.setDeletedBy(deletedBy);
        restaurant.setDeletedAt(Instant.now());
        
        restaurantRepository.save(restaurant);
        log.info("Restaurant deleted successfully: {}", id);
    }
    
    /**
     * Add new outlet to restaurant.
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public OutletResponse addOutlet(String restaurantId, CreateOutletRequest request) {
        log.info("Adding outlet to restaurant: {}", restaurantId);
        
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId, true));
        
        // Map to outlet entity
        RestaurantOutlet outlet = restaurantMapper.toOutletEntity(request);
        
        // Add to restaurant's outlets
        if (restaurant.getOutlets() == null) {
            restaurant.setOutlets(new ArrayList<>());
        }
        restaurant.getOutlets().add(outlet);
        
        // Save
        restaurantRepository.save(restaurant);
        log.info("Outlet added successfully: {}", outlet.getOutletId());
        
        // Publish event
        publishOutletEvent("outlet.created", restaurantId, outlet);
        
        return restaurantMapper.toOutletResponse(outlet);
    }
    
    /**
     * Get outlet by ID.
     */
    public OutletResponse getOutletById(String restaurantId, String outletId) {
        log.debug("Fetching outlet: {} from restaurant: {}", outletId, restaurantId);
        
        Restaurant restaurant = restaurantRepository.findByOutletId(outletId)
            .orElseThrow(() -> new OutletNotFoundException(outletId, true));
        
        RestaurantOutlet outlet = restaurant.getOutlets().stream()
            .filter(o -> o.getOutletId().equals(outletId) && !o.isDeleted())
            .findFirst()
            .orElseThrow(() -> new OutletNotFoundException(outletId, true));
        
        return restaurantMapper.toOutletResponse(outlet);
    }
    
    /**
     * Get all outlets for a restaurant.
     */
    public List<OutletResponse> getOutletsByRestaurant(String restaurantId) {
        log.debug("Fetching outlets for restaurant: {}", restaurantId);
        
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId, true));
        
        List<RestaurantOutlet> activeOutlets = restaurant.getOutlets().stream()
            .filter(outlet -> !outlet.isDeleted())
            .toList();
        
        return restaurantMapper.toOutletResponseList(activeOutlets);
    }
    
    /**
     * Approve restaurant brand.
     */
    @Transactional
    @CacheEvict(value = "restaurants", key = "#restaurantId")
    public RestaurantResponse approveRestaurant(String restaurantId, String approvedBy) {
        log.info("Approving restaurant: {}", restaurantId);
        
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedFalse(restaurantId)
            .orElseThrow(() -> new RestaurantNotFoundException(restaurantId, true));
        
        restaurant.setStatus(RestaurantStatus.APPROVED);
        restaurant.setUpdatedBy(approvedBy);
        restaurant.setUpdatedAt(Instant.now());
        
        Restaurant saved = restaurantRepository.save(restaurant);
        
        // Publish event
        publishRestaurantEvent("restaurant.approved", saved);
        
        return restaurantMapper.toResponse(saved);
    }
    
    /**
     * Validate that ownership percentages sum to 100.
     */
    private void validateOwnershipPercentages(Restaurant restaurant) {
        double totalPercentage = restaurant.getOwners().stream()
            .mapToDouble(owner -> owner.getOwnershipPercentage() != null ? owner.getOwnershipPercentage() : 0.0)
            .sum();
        
        if (Math.abs(totalPercentage - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                String.format("Total ownership percentage must equal 100%%, but was %.2f%%", totalPercentage)
            );
        }
    }
    
    /**
     * Publish restaurant event to Kafka.
     */
    private void publishRestaurantEvent(String topic, Restaurant restaurant) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", java.util.UUID.randomUUID().toString());
            event.put("eventType", topic);
            event.put("timestamp", Instant.now().toString());
            event.put("restaurantId", restaurant.getId());
            event.put("name", restaurant.getName());
            event.put("status", restaurant.getStatus());
            
            kafkaTemplate.send(topic, restaurant.getId(), event);
            log.debug("Published event to {}: {}", topic, restaurant.getId());
        } catch (Exception e) {
            log.error("Failed to publish event to {}", topic, e);
            // Don't fail the operation if event publishing fails
        }
    }
    
    /**
     * Publish outlet event to Kafka.
     */
    private void publishOutletEvent(String topic, String restaurantId, RestaurantOutlet outlet) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", java.util.UUID.randomUUID().toString());
            event.put("eventType", topic);
            event.put("timestamp", Instant.now().toString());
            event.put("restaurantId", restaurantId);
            event.put("outletId", outlet.getOutletId());
            event.put("outletName", outlet.getOutletName());
            event.put("status", outlet.getStatus());
            
            kafkaTemplate.send(topic, outlet.getOutletId(), event);
            log.debug("Published event to {}: {}", topic, outlet.getOutletId());
        } catch (Exception e) {
            log.error("Failed to publish event to {}", topic, e);
        }
    }
    
    /**
     * Search for restaurants or outlets based on multiple criteria.
     * Returns different response types based on returnOutlets flag.
     * 
     * @param request Search criteria with filters and pagination
     * @return Page of search results (either restaurants or outlets)
     */
    @Cacheable(value = "restaurantSearch", key = "#request.hashCode()")
    public Page<?> searchRestaurants(SearchRestaurantRequest request) {
        log.info("Searching restaurants: pincode={}, cuisine={}, budget={}, returnOutlets={}", 
                request.getDeliveryPincode(), request.getCuisineTypes(), 
                request.getBudgetType(), request.getReturnOutlets());
        
        // Validate request
        validateSearchRequest(request);
        
        // Build pageable with sorting
        Pageable pageable = buildPageable(request);
        
        // Execute search based on return type
        if (Boolean.TRUE.equals(request.getReturnOutlets())) {
            Page<RestaurantOutlet> outlets = restaurantSearchRepository.searchOutlets(request, pageable);
            return outlets.map(outlet -> searchMapper.toOutletSearchResponse(outlet, request.getDeliveryPincode()));
        } else {
            Page<Restaurant> restaurants = restaurantSearchRepository.searchRestaurants(request, pageable);
            return restaurants.map(restaurant -> searchMapper.toRestaurantSearchResponse(restaurant, request.getDeliveryPincode()));
        }
    }
    
    /**
     * Validate search request parameters.
     */
    private void validateSearchRequest(SearchRestaurantRequest request) {
        if (!request.isValidRatingRange()) {
            throw new IllegalArgumentException("Invalid rating range: minRating must be <= maxRating");
        }
        if (!request.isValidPriceRange()) {
            throw new IllegalArgumentException("Invalid price range: minPrice must be <= maxPrice");
        }
    }
    
    /**
     * Build pageable with sorting based on request parameters.
     */
    private Pageable buildPageable(SearchRestaurantRequest request) {
        Sort sort = buildSort(request.getSortBy(), request.getSortDirection());
        return PageRequest.of(request.getPage(), request.getSize(), sort);
    }
    
    /**
     * Build sort object based on sort field and direction.
     */
    private Sort buildSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
        
        return switch (sortBy.toLowerCase()) {
            case "rating" -> Sort.by(direction, "averageRating");
            case "price" -> Sort.by(direction, "averageCostForTwo");
            case "popularity" -> Sort.by(direction, "totalOrders");
            case "distance" -> Sort.by(direction, "outlets.address.pincode"); // Approximate
            default -> Sort.by(Sort.Direction.DESC, "averageRating");
        };
    }
}


