package com.foodai.restaurant.mapper;

import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.dto.request.*;
import com.foodai.restaurant.dto.response.*;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for Restaurant entity and DTOs.
 * Handles mapping between domain models and request/response DTOs.
 */
@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RestaurantMapper {
    
    // ========== Restaurant Mappings ==========
    
    /**
     * Map CreateRestaurantRequest to Restaurant entity.
     */
    @Mapping(target = "id", expression = "java(generateId())")
    @Mapping(target = "outlets", ignore = true)
    @Mapping(target = "averageRating", constant = "0.0")
    @Mapping(target = "totalReviews", constant = "0")
    @Mapping(target = "totalOrders", constant = "0")
    @Mapping(target = "acceptsOrders", constant = "true")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", expression = "java(now())")
    @Mapping(target = "updatedAt", expression = "java(now())")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "onboardingType", constant = "SELF_SERVICE")
    Restaurant toEntity(CreateRestaurantRequest request);
    
    /**
     * Map Restaurant entity to RestaurantResponse.
     */
    RestaurantResponse toResponse(Restaurant restaurant);
    
    /**
     * Map list of Restaurant to list of RestaurantResponse.
     */
    List<RestaurantResponse> toResponseList(List<Restaurant> restaurants);
    
    // ========== Outlet Mappings ==========
    
    /**
     * Map CreateOutletRequest to RestaurantOutlet entity.
     */
    @Mapping(target = "outletId", expression = "java(generateId())")
    @Mapping(target = "status", constant = "NOT_ONBOARDED")
    @Mapping(target = "acceptsOrders", constant = "false")
    @Mapping(target = "averageRating", constant = "0.0")
    @Mapping(target = "totalReviews", constant = "0")
    @Mapping(target = "totalOrders", constant = "0")
    @Mapping(target = "currentOrderQueue", constant = "0")
    @Mapping(target = "createdAt", expression = "java(now())")
    @Mapping(target = "updatedAt", expression = "java(now())")
    @Mapping(target = "deleted", constant = "false")
    RestaurantOutlet toOutletEntity(CreateOutletRequest request);
    
    /**
     * Map RestaurantOutlet to OutletResponse.
     */
    OutletResponse toOutletResponse(RestaurantOutlet outlet);
    
    /**
     * Map list of outlets to response list.
     */
    List<OutletResponse> toOutletResponseList(List<RestaurantOutlet> outlets);
    
    // ========== Value Object Mappings ==========
    
    OwnerVO toOwnerVO(OwnerDTO dto);
    OwnerResponse toOwnerResponse(OwnerVO vo);
    List<OwnerVO> toOwnerVOList(List<OwnerDTO> dtos);
    List<OwnerResponse> toOwnerResponseList(List<OwnerVO> vos);
    
    ContactVO toContactVO(ContactDTO dto);
    ContactResponse toContactResponse(ContactVO vo);
    List<ContactVO> toContactVOList(List<ContactDTO> dtos);
    List<ContactResponse> toContactResponseList(List<ContactVO> vos);
    
    @Mapping(target = "verificationStatus", constant = "PENDING")
    @Mapping(target = "uploadedAt", expression = "java(now())")
    DocumentVO toDocumentVO(DocumentDTO dto);
    DocumentResponse toDocumentResponse(DocumentVO vo);
    List<DocumentVO> toDocumentVOList(List<DocumentDTO> dtos);
    List<DocumentResponse> toDocumentResponseList(List<DocumentVO> vos);
    
    AddressVO toAddressVO(AddressDTO dto);
    AddressResponse toAddressResponse(AddressVO vo);

    @Mapping(target = "accountNumberLast4", expression = "java(maskAccountNumber(vo != null ? vo.getAccountNumber() : null))")
    BankAccountResponse toBankAccountResponse(com.foodai.restaurant.domain.model.BankAccountVO vo);

    default String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 4) return null;
        return accountNumber.substring(accountNumber.length() - 4);
    }
    
    OperatingHoursVO toOperatingHoursVO(OperatingHoursDTO dto);
    OperatingHoursResponse toOperatingHoursResponse(OperatingHoursVO vo);
    List<OperatingHoursVO> toOperatingHoursVOList(List<OperatingHoursDTO> dtos);
    List<OperatingHoursResponse> toOperatingHoursResponseList(List<OperatingHoursVO> vos);
    
    // ========== Contract Mappings ==========
    
    @Mapping(target = "contractId", expression = "java(generateId())")
    @Mapping(target = "signed", constant = "false")
    @Mapping(target = "validFrom", expression = "java(now())")
    @Mapping(target = "createdAt", expression = "java(now())")
    @Mapping(target = "updatedAt", expression = "java(now())")
    ContractVO toContractVO(ContractDTO dto);
    
    ContractResponse toContractResponse(ContractVO vo);
    
    PlatformFeeConfig toPlatformFeeConfig(PlatformFeeDTO dto);
    DeliveryFeeConfig toDeliveryFeeConfig(DeliveryFeeDTO dto);
    PaymentGatewayFeeConfig toPaymentGatewayFeeConfig(PaymentGatewayFeeDTO dto);
    
    // ========== Serviceability Mappings ==========
    
    @Mapping(target = "rules.excludedPincodes", ignore = true)
    ServiceabilityConfig toServiceabilityConfig(ServiceabilityConfigDTO dto);
    
    ServiceabilityResponse toServiceabilityResponse(ServiceabilityConfig config);
    
    // ========== TAT Mappings ==========
    
    @Mapping(target = "peakHours", ignore = true)
    TATConfig toTATConfig(TATConfigDTO dto);
    
    TATConfigResponse toTATConfigResponse(TATConfig config);
    
    // ========== Helper Methods ==========
    
    default String generateId() {
        return UUID.randomUUID().toString();
    }
    
    default Instant now() {
        return Instant.now();
    }
    
    /**
     * Update Restaurant entity from request, preserving system fields.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "outlets", ignore = true)
    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalReviews", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(now())")
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    void updateEntityFromRequest(@MappingTarget Restaurant restaurant, CreateRestaurantRequest request);
}


