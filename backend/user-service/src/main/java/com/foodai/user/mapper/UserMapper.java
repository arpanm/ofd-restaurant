package com.foodai.user.mapper;

import com.foodai.user.domain.model.*;
import com.foodai.user.dto.request.*;
import com.foodai.user.dto.response.*;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * MapStruct mapper for User entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    /**
     * Maps create request DTO to User entity.
     *
     * @param request the create request
     * @return the User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "paymentMethods", ignore = true)
    @Mapping(target = "favoriteRestaurantIds", ignore = true)
    @Mapping(target = "favoriteMenuItemIds", ignore = true)
    @Mapping(target = "recentSearches", ignore = true)
    @Mapping(target = "referralCode", expression = "java(generateReferralCode())")
    @Mapping(target = "status", constant = "PENDING_VERIFICATION")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "phoneVerified", constant = "false")
    @Mapping(target = "totalOrders", constant = "0")
    @Mapping(target = "totalSpent", constant = "0.0")
    @Mapping(target = "averageOrderValue", constant = "0.0")
    @Mapping(target = "deleted", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "defaultAddressId", ignore = true)
    @Mapping(target = "defaultPaymentMethodId", ignore = true)
    @Mapping(target = "notificationPreferences", ignore = true)
    @Mapping(target = "personalizationSettings", ignore = true)
    @Mapping(target = "dietaryPreferences", source = "dietaryPreferences")
    User toEntity(CreateUserRequest request);

    /**
     * Updates User entity from update request.
     *
     * @param user    the user to update
     * @param request the update request
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(target = "paymentMethods", ignore = true)
    @Mapping(target = "favoriteRestaurantIds", ignore = true)
    @Mapping(target = "favoriteMenuItemIds", ignore = true)
    @Mapping(target = "recentSearches", ignore = true)
    @Mapping(target = "referralCode", ignore = true)
    @Mapping(target = "referredBy", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "phoneVerified", ignore = true)
    @Mapping(target = "totalOrders", ignore = true)
    @Mapping(target = "totalSpent", ignore = true)
    @Mapping(target = "averageOrderValue", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "defaultAddressId", ignore = true)
    @Mapping(target = "defaultPaymentMethodId", ignore = true)
    void updateEntityFromRequest(@MappingTarget User user, UpdateUserRequest request);

    /**
     * Maps User entity to response DTO.
     *
     * @param user the User entity
     * @return the response DTO
     */
    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    UserResponse toResponse(User user);

    /**
     * Maps list of User entities to response DTOs.
     *
     * @param users the list of users
     * @return list of response DTOs
     */
    List<UserResponse> toResponseList(List<User> users);

    // ==================== Address Mappings ====================

    /**
     * Maps AddressDTO to AddressVO.
     *
     * @param dto the address DTO
     * @return the AddressVO
     */
    @Mapping(target = "id", expression = "java(dto.getId() != null ? dto.getId() : generateAddressId())")
    @Mapping(target = "verified", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "isDefault", source = "isDefault", defaultValue = "false")
    AddressVO toAddressVO(AddressDTO dto);

    /**
     * Maps AddressVO to AddressResponse.
     *
     * @param vo the address VO
     * @return the response DTO
     */
    @Mapping(target = "formattedAddress", expression = "java(vo.getFormattedAddress())")
    AddressResponse toAddressResponse(AddressVO vo);

    List<AddressResponse> toAddressResponseList(List<AddressVO> addresses);

    // ==================== Dietary Preferences Mappings ====================

    DietaryPreferencesVO toDietaryPreferencesVO(DietaryPreferencesDTO dto);

    @Mapping(target = "hasRestrictions", expression = "java(vo != null && vo.hasRestrictions())")
    @Mapping(target = "activeDietaryFlags", expression = "java(vo != null ? vo.getActiveDietaryFlags() : java.util.Collections.emptyList())")
    DietaryPreferencesResponse toDietaryPreferencesResponse(DietaryPreferencesVO vo);

    // ==================== Notification Preferences Mappings ====================

    NotificationPreferencesVO toNotificationPreferencesVO(NotificationPreferencesDTO dto);

    NotificationPreferencesResponse toNotificationPreferencesResponse(NotificationPreferencesVO vo);

    // ==================== Personalization Settings Mappings ====================

    PersonalizationSettingsVO toPersonalizationSettingsVO(PersonalizationSettingsDTO dto);

    PersonalizationSettingsResponse toPersonalizationSettingsResponse(PersonalizationSettingsVO vo);

    // ==================== Helper Methods ====================

    /**
     * Generates a unique referral code.
     *
     * @return referral code
     */
    default String generateReferralCode() {
        return "FD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Generates a unique address ID.
     *
     * @return address ID
     */
    default String generateAddressId() {
        return "addr_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}

