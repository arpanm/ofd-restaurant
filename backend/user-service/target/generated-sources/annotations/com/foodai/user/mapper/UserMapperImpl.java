package com.foodai.user.mapper;

import com.foodai.user.domain.model.AddressVO;
import com.foodai.user.domain.model.DietaryPreferencesVO;
import com.foodai.user.domain.model.NotificationPreferencesVO;
import com.foodai.user.domain.model.PersonalizationSettingsVO;
import com.foodai.user.domain.model.User;
import com.foodai.user.domain.model.UserStatus;
import com.foodai.user.dto.request.AddressDTO;
import com.foodai.user.dto.request.CreateUserRequest;
import com.foodai.user.dto.request.DietaryPreferencesDTO;
import com.foodai.user.dto.request.NotificationPreferencesDTO;
import com.foodai.user.dto.request.PersonalizationSettingsDTO;
import com.foodai.user.dto.request.UpdateUserRequest;
import com.foodai.user.dto.response.AddressResponse;
import com.foodai.user.dto.response.DietaryPreferencesResponse;
import com.foodai.user.dto.response.NotificationPreferencesResponse;
import com.foodai.user.dto.response.PersonalizationSettingsResponse;
import com.foodai.user.dto.response.UserResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T15:29:47+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(CreateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.dietaryPreferences( toDietaryPreferencesVO( request.getDietaryPreferences() ) );
        user.avatarUrl( request.getAvatarUrl() );
        user.createdBy( request.getCreatedBy() );
        List<String> list = request.getCuisinePreferences();
        if ( list != null ) {
            user.cuisinePreferences( new ArrayList<String>( list ) );
        }
        user.dateOfBirth( request.getDateOfBirth() );
        user.displayName( request.getDisplayName() );
        user.email( request.getEmail() );
        user.firstName( request.getFirstName() );
        user.gender( request.getGender() );
        user.languagePreference( request.getLanguagePreference() );
        user.lastName( request.getLastName() );
        user.phone( request.getPhone() );
        user.referredBy( request.getReferredBy() );
        user.timezone( request.getTimezone() );

        user.referralCode( generateReferralCode() );
        user.status( UserStatus.PENDING_VERIFICATION );
        user.emailVerified( false );
        user.phoneVerified( false );
        user.totalOrders( 0 );
        user.totalSpent( (double) 0.0 );
        user.averageOrderValue( (double) 0.0 );
        user.deleted( false );

        return user.build();
    }

    @Override
    public void updateEntityFromRequest(User user, UpdateUserRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getAvatarUrl() != null ) {
            user.setAvatarUrl( request.getAvatarUrl() );
        }
        if ( user.getCuisinePreferences() != null ) {
            List<String> list = request.getCuisinePreferences();
            if ( list != null ) {
                user.getCuisinePreferences().clear();
                user.getCuisinePreferences().addAll( list );
            }
        }
        else {
            List<String> list = request.getCuisinePreferences();
            if ( list != null ) {
                user.setCuisinePreferences( new ArrayList<String>( list ) );
            }
        }
        if ( request.getDateOfBirth() != null ) {
            user.setDateOfBirth( request.getDateOfBirth() );
        }
        if ( request.getDietaryPreferences() != null ) {
            user.setDietaryPreferences( toDietaryPreferencesVO( request.getDietaryPreferences() ) );
        }
        if ( request.getDisplayName() != null ) {
            user.setDisplayName( request.getDisplayName() );
        }
        if ( request.getEmail() != null ) {
            user.setEmail( request.getEmail() );
        }
        if ( request.getFirstName() != null ) {
            user.setFirstName( request.getFirstName() );
        }
        if ( request.getGender() != null ) {
            user.setGender( request.getGender() );
        }
        if ( request.getLanguagePreference() != null ) {
            user.setLanguagePreference( request.getLanguagePreference() );
        }
        if ( request.getLastName() != null ) {
            user.setLastName( request.getLastName() );
        }
        if ( request.getNotificationPreferences() != null ) {
            user.setNotificationPreferences( toNotificationPreferencesVO( request.getNotificationPreferences() ) );
        }
        if ( request.getPersonalizationSettings() != null ) {
            user.setPersonalizationSettings( toPersonalizationSettingsVO( request.getPersonalizationSettings() ) );
        }
        if ( request.getTimezone() != null ) {
            user.setTimezone( request.getTimezone() );
        }
        if ( request.getUpdatedBy() != null ) {
            user.setUpdatedBy( request.getUpdatedBy() );
        }
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.addresses( toAddressResponseList( user.getAddresses() ) );
        userResponse.avatarUrl( user.getAvatarUrl() );
        userResponse.averageOrderValue( user.getAverageOrderValue() );
        userResponse.createdAt( user.getCreatedAt() );
        List<String> list1 = user.getCuisinePreferences();
        if ( list1 != null ) {
            userResponse.cuisinePreferences( new ArrayList<String>( list1 ) );
        }
        userResponse.dateOfBirth( user.getDateOfBirth() );
        userResponse.defaultAddressId( user.getDefaultAddressId() );
        userResponse.defaultPaymentMethodId( user.getDefaultPaymentMethodId() );
        userResponse.dietaryPreferences( toDietaryPreferencesResponse( user.getDietaryPreferences() ) );
        userResponse.displayName( user.getDisplayName() );
        userResponse.email( user.getEmail() );
        userResponse.emailVerified( user.isEmailVerified() );
        List<String> list2 = user.getFavoriteMenuItemIds();
        if ( list2 != null ) {
            userResponse.favoriteMenuItemIds( new ArrayList<String>( list2 ) );
        }
        List<String> list3 = user.getFavoriteRestaurantIds();
        if ( list3 != null ) {
            userResponse.favoriteRestaurantIds( new ArrayList<String>( list3 ) );
        }
        userResponse.firstName( user.getFirstName() );
        userResponse.gender( user.getGender() );
        userResponse.id( user.getId() );
        userResponse.languagePreference( user.getLanguagePreference() );
        userResponse.lastLoginAt( user.getLastLoginAt() );
        userResponse.lastName( user.getLastName() );
        userResponse.notificationPreferences( toNotificationPreferencesResponse( user.getNotificationPreferences() ) );
        userResponse.personalizationSettings( toPersonalizationSettingsResponse( user.getPersonalizationSettings() ) );
        userResponse.phone( user.getPhone() );
        userResponse.phoneVerified( user.isPhoneVerified() );
        userResponse.referralCode( user.getReferralCode() );
        userResponse.status( user.getStatus() );
        userResponse.timezone( user.getTimezone() );
        userResponse.totalOrders( user.getTotalOrders() );
        userResponse.totalSpent( user.getTotalSpent() );
        userResponse.updatedAt( user.getUpdatedAt() );

        userResponse.fullName( user.getFullName() );

        return userResponse.build();
    }

    @Override
    public List<UserResponse> toResponseList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponse> list = new ArrayList<UserResponse>( users.size() );
        for ( User user : users ) {
            list.add( toResponse( user ) );
        }

        return list;
    }

    @Override
    public AddressVO toAddressVO(AddressDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AddressVO.AddressVOBuilder addressVO = AddressVO.builder();

        if ( dto.getIsDefault() != null ) {
            addressVO.isDefault( dto.getIsDefault() );
        }
        else {
            addressVO.isDefault( false );
        }
        addressVO.area( dto.getArea() );
        addressVO.building( dto.getBuilding() );
        addressVO.city( dto.getCity() );
        addressVO.contactPhone( dto.getContactPhone() );
        addressVO.country( dto.getCountry() );
        addressVO.deliveryInstructions( dto.getDeliveryInstructions() );
        addressVO.flatNumber( dto.getFlatNumber() );
        addressVO.label( dto.getLabel() );
        addressVO.landmark( dto.getLandmark() );
        addressVO.latitude( dto.getLatitude() );
        addressVO.longitude( dto.getLongitude() );
        addressVO.pincode( dto.getPincode() );
        addressVO.plusCode( dto.getPlusCode() );
        addressVO.recipientName( dto.getRecipientName() );
        addressVO.state( dto.getState() );
        addressVO.street( dto.getStreet() );
        addressVO.street2( dto.getStreet2() );
        addressVO.type( dto.getType() );

        addressVO.id( dto.getId() != null ? dto.getId() : generateAddressId() );
        addressVO.verified( false );
        addressVO.createdAt( java.time.Instant.now() );
        addressVO.updatedAt( java.time.Instant.now() );

        return addressVO.build();
    }

    @Override
    public AddressResponse toAddressResponse(AddressVO vo) {
        if ( vo == null ) {
            return null;
        }

        AddressResponse.AddressResponseBuilder addressResponse = AddressResponse.builder();

        addressResponse.area( vo.getArea() );
        addressResponse.building( vo.getBuilding() );
        addressResponse.city( vo.getCity() );
        addressResponse.contactPhone( vo.getContactPhone() );
        addressResponse.country( vo.getCountry() );
        addressResponse.createdAt( vo.getCreatedAt() );
        addressResponse.deliveryInstructions( vo.getDeliveryInstructions() );
        addressResponse.flatNumber( vo.getFlatNumber() );
        addressResponse.id( vo.getId() );
        addressResponse.label( vo.getLabel() );
        addressResponse.landmark( vo.getLandmark() );
        addressResponse.latitude( vo.getLatitude() );
        addressResponse.longitude( vo.getLongitude() );
        addressResponse.pincode( vo.getPincode() );
        addressResponse.recipientName( vo.getRecipientName() );
        addressResponse.state( vo.getState() );
        addressResponse.street( vo.getStreet() );
        addressResponse.type( vo.getType() );
        addressResponse.verified( vo.isVerified() );

        addressResponse.formattedAddress( vo.getFormattedAddress() );

        return addressResponse.build();
    }

    @Override
    public List<AddressResponse> toAddressResponseList(List<AddressVO> addresses) {
        if ( addresses == null ) {
            return null;
        }

        List<AddressResponse> list = new ArrayList<AddressResponse>( addresses.size() );
        for ( AddressVO addressVO : addresses ) {
            list.add( toAddressResponse( addressVO ) );
        }

        return list;
    }

    @Override
    public DietaryPreferencesVO toDietaryPreferencesVO(DietaryPreferencesDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DietaryPreferencesVO.DietaryPreferencesVOBuilder dietaryPreferencesVO = DietaryPreferencesVO.builder();

        List<String> list = dto.getAllergies();
        if ( list != null ) {
            dietaryPreferencesVO.allergies( new ArrayList<String>( list ) );
        }
        List<String> list1 = dto.getAvoidIngredients();
        if ( list1 != null ) {
            dietaryPreferencesVO.avoidIngredients( new ArrayList<String>( list1 ) );
        }
        dietaryPreferencesVO.dailyCalorieTarget( dto.getDailyCalorieTarget() );
        dietaryPreferencesVO.dailyProteinTarget( dto.getDailyProteinTarget() );
        if ( dto.getDairyFree() != null ) {
            dietaryPreferencesVO.dairyFree( dto.getDairyFree() );
        }
        List<String> list2 = dto.getDefaultCustomizations();
        if ( list2 != null ) {
            dietaryPreferencesVO.defaultCustomizations( new ArrayList<String>( list2 ) );
        }
        if ( dto.getGlutenFree() != null ) {
            dietaryPreferencesVO.glutenFree( dto.getGlutenFree() );
        }
        if ( dto.getHalal() != null ) {
            dietaryPreferencesVO.halal( dto.getHalal() );
        }
        List<String> list3 = dto.getHealthGoals();
        if ( list3 != null ) {
            dietaryPreferencesVO.healthGoals( new ArrayList<String>( list3 ) );
        }
        if ( dto.getJain() != null ) {
            dietaryPreferencesVO.jain( dto.getJain() );
        }
        if ( dto.getKeto() != null ) {
            dietaryPreferencesVO.keto( dto.getKeto() );
        }
        if ( dto.getKosher() != null ) {
            dietaryPreferencesVO.kosher( dto.getKosher() );
        }
        if ( dto.getLowCarb() != null ) {
            dietaryPreferencesVO.lowCarb( dto.getLowCarb() );
        }
        if ( dto.getPaleo() != null ) {
            dietaryPreferencesVO.paleo( dto.getPaleo() );
        }
        if ( dto.getShowCalories() != null ) {
            dietaryPreferencesVO.showCalories( dto.getShowCalories() );
        }
        if ( dto.getShowNutrition() != null ) {
            dietaryPreferencesVO.showNutrition( dto.getShowNutrition() );
        }
        dietaryPreferencesVO.spicePreference( dto.getSpicePreference() );
        if ( dto.getVegan() != null ) {
            dietaryPreferencesVO.vegan( dto.getVegan() );
        }
        if ( dto.getVegetarian() != null ) {
            dietaryPreferencesVO.vegetarian( dto.getVegetarian() );
        }

        return dietaryPreferencesVO.build();
    }

    @Override
    public DietaryPreferencesResponse toDietaryPreferencesResponse(DietaryPreferencesVO vo) {
        if ( vo == null ) {
            return null;
        }

        DietaryPreferencesResponse.DietaryPreferencesResponseBuilder dietaryPreferencesResponse = DietaryPreferencesResponse.builder();

        List<String> list = vo.getAllergies();
        if ( list != null ) {
            dietaryPreferencesResponse.allergies( new ArrayList<String>( list ) );
        }
        List<String> list1 = vo.getAvoidIngredients();
        if ( list1 != null ) {
            dietaryPreferencesResponse.avoidIngredients( new ArrayList<String>( list1 ) );
        }
        dietaryPreferencesResponse.dailyCalorieTarget( vo.getDailyCalorieTarget() );
        dietaryPreferencesResponse.dairyFree( vo.isDairyFree() );
        List<String> list2 = vo.getDefaultCustomizations();
        if ( list2 != null ) {
            dietaryPreferencesResponse.defaultCustomizations( new ArrayList<String>( list2 ) );
        }
        dietaryPreferencesResponse.glutenFree( vo.isGlutenFree() );
        dietaryPreferencesResponse.halal( vo.isHalal() );
        List<String> list3 = vo.getHealthGoals();
        if ( list3 != null ) {
            dietaryPreferencesResponse.healthGoals( new ArrayList<String>( list3 ) );
        }
        dietaryPreferencesResponse.jain( vo.isJain() );
        dietaryPreferencesResponse.keto( vo.isKeto() );
        dietaryPreferencesResponse.kosher( vo.isKosher() );
        dietaryPreferencesResponse.lowCarb( vo.isLowCarb() );
        dietaryPreferencesResponse.paleo( vo.isPaleo() );
        dietaryPreferencesResponse.spicePreference( vo.getSpicePreference() );
        dietaryPreferencesResponse.vegan( vo.isVegan() );
        dietaryPreferencesResponse.vegetarian( vo.isVegetarian() );

        dietaryPreferencesResponse.hasRestrictions( vo != null && vo.hasRestrictions() );
        dietaryPreferencesResponse.activeDietaryFlags( vo != null ? vo.getActiveDietaryFlags() : java.util.Collections.emptyList() );

        return dietaryPreferencesResponse.build();
    }

    @Override
    public NotificationPreferencesVO toNotificationPreferencesVO(NotificationPreferencesDTO dto) {
        if ( dto == null ) {
            return null;
        }

        NotificationPreferencesVO.NotificationPreferencesVOBuilder notificationPreferencesVO = NotificationPreferencesVO.builder();

        if ( dto.getAccountAlerts() != null ) {
            notificationPreferencesVO.accountAlerts( dto.getAccountAlerts() );
        }
        if ( dto.getDietReminders() != null ) {
            notificationPreferencesVO.dietReminders( dto.getDietReminders() );
        }
        if ( dto.getEmailEnabled() != null ) {
            notificationPreferencesVO.emailEnabled( dto.getEmailEnabled() );
        }
        if ( dto.getLiveTrackingUpdates() != null ) {
            notificationPreferencesVO.liveTrackingUpdates( dto.getLiveTrackingUpdates() );
        }
        if ( dto.getLoyaltyUpdates() != null ) {
            notificationPreferencesVO.loyaltyUpdates( dto.getLoyaltyUpdates() );
        }
        notificationPreferencesVO.nearbyAlertDistance( dto.getNearbyAlertDistance() );
        if ( dto.getNewRestaurants() != null ) {
            notificationPreferencesVO.newRestaurants( dto.getNewRestaurants() );
        }
        if ( dto.getOrderUpdates() != null ) {
            notificationPreferencesVO.orderUpdates( dto.getOrderUpdates() );
        }
        if ( dto.getPriceAlerts() != null ) {
            notificationPreferencesVO.priceAlerts( dto.getPriceAlerts() );
        }
        if ( dto.getPromotions() != null ) {
            notificationPreferencesVO.promotions( dto.getPromotions() );
        }
        if ( dto.getPushEnabled() != null ) {
            notificationPreferencesVO.pushEnabled( dto.getPushEnabled() );
        }
        if ( dto.getQuietHoursEnabled() != null ) {
            notificationPreferencesVO.quietHoursEnabled( dto.getQuietHoursEnabled() );
        }
        notificationPreferencesVO.quietHoursEnd( dto.getQuietHoursEnd() );
        notificationPreferencesVO.quietHoursStart( dto.getQuietHoursStart() );
        if ( dto.getRecommendations() != null ) {
            notificationPreferencesVO.recommendations( dto.getRecommendations() );
        }
        if ( dto.getSmsEnabled() != null ) {
            notificationPreferencesVO.smsEnabled( dto.getSmsEnabled() );
        }
        if ( dto.getWhatsappEnabled() != null ) {
            notificationPreferencesVO.whatsappEnabled( dto.getWhatsappEnabled() );
        }

        return notificationPreferencesVO.build();
    }

    @Override
    public NotificationPreferencesResponse toNotificationPreferencesResponse(NotificationPreferencesVO vo) {
        if ( vo == null ) {
            return null;
        }

        NotificationPreferencesResponse.NotificationPreferencesResponseBuilder notificationPreferencesResponse = NotificationPreferencesResponse.builder();

        notificationPreferencesResponse.dietReminders( vo.isDietReminders() );
        notificationPreferencesResponse.emailEnabled( vo.isEmailEnabled() );
        notificationPreferencesResponse.liveTrackingUpdates( vo.isLiveTrackingUpdates() );
        notificationPreferencesResponse.loyaltyUpdates( vo.isLoyaltyUpdates() );
        notificationPreferencesResponse.nearbyAlertDistance( vo.getNearbyAlertDistance() );
        notificationPreferencesResponse.orderUpdates( vo.isOrderUpdates() );
        notificationPreferencesResponse.promotions( vo.isPromotions() );
        notificationPreferencesResponse.pushEnabled( vo.isPushEnabled() );
        notificationPreferencesResponse.quietHoursEnabled( vo.isQuietHoursEnabled() );
        notificationPreferencesResponse.quietHoursEnd( vo.getQuietHoursEnd() );
        notificationPreferencesResponse.quietHoursStart( vo.getQuietHoursStart() );
        notificationPreferencesResponse.recommendations( vo.isRecommendations() );
        notificationPreferencesResponse.smsEnabled( vo.isSmsEnabled() );
        notificationPreferencesResponse.whatsappEnabled( vo.isWhatsappEnabled() );

        return notificationPreferencesResponse.build();
    }

    @Override
    public PersonalizationSettingsVO toPersonalizationSettingsVO(PersonalizationSettingsDTO dto) {
        if ( dto == null ) {
            return null;
        }

        PersonalizationSettingsVO.PersonalizationSettingsVOBuilder personalizationSettingsVO = PersonalizationSettingsVO.builder();

        if ( dto.getAiChatEnabled() != null ) {
            personalizationSettingsVO.aiChatEnabled( dto.getAiChatEnabled() );
        }
        if ( dto.getAiRecommendationsEnabled() != null ) {
            personalizationSettingsVO.aiRecommendationsEnabled( dto.getAiRecommendationsEnabled() );
        }
        if ( dto.getChatAutoOrderEnabled() != null ) {
            personalizationSettingsVO.chatAutoOrderEnabled( dto.getChatAutoOrderEnabled() );
        }
        personalizationSettingsVO.chatLanguage( dto.getChatLanguage() );
        personalizationSettingsVO.chatOrderBudgetLimit( dto.getChatOrderBudgetLimit() );
        personalizationSettingsVO.chatStyle( dto.getChatStyle() );
        personalizationSettingsVO.dietaryWeight( dto.getDietaryWeight() );
        if ( dto.getLearningFromHistoryEnabled() != null ) {
            personalizationSettingsVO.learningFromHistoryEnabled( dto.getLearningFromHistoryEnabled() );
        }
        if ( dto.getLocationBasedPersonalization() != null ) {
            personalizationSettingsVO.locationBasedPersonalization( dto.getLocationBasedPersonalization() );
        }
        personalizationSettingsVO.orderHistoryWeight( dto.getOrderHistoryWeight() );
        if ( dto.getShareDataForImprovement() != null ) {
            personalizationSettingsVO.shareDataForImprovement( dto.getShareDataForImprovement() );
        }
        if ( dto.getShowPersonalizedBanners() != null ) {
            personalizationSettingsVO.showPersonalizedBanners( dto.getShowPersonalizedBanners() );
        }
        if ( dto.getShowQuickReorder() != null ) {
            personalizationSettingsVO.showQuickReorder( dto.getShowQuickReorder() );
        }
        if ( dto.getShowSuggestions() != null ) {
            personalizationSettingsVO.showSuggestions( dto.getShowSuggestions() );
        }
        if ( dto.getSmartReorderEnabled() != null ) {
            personalizationSettingsVO.smartReorderEnabled( dto.getSmartReorderEnabled() );
        }
        if ( dto.getVoiceOrderingEnabled() != null ) {
            personalizationSettingsVO.voiceOrderingEnabled( dto.getVoiceOrderingEnabled() );
        }

        return personalizationSettingsVO.build();
    }

    @Override
    public PersonalizationSettingsResponse toPersonalizationSettingsResponse(PersonalizationSettingsVO vo) {
        if ( vo == null ) {
            return null;
        }

        PersonalizationSettingsResponse.PersonalizationSettingsResponseBuilder personalizationSettingsResponse = PersonalizationSettingsResponse.builder();

        personalizationSettingsResponse.aiChatEnabled( vo.isAiChatEnabled() );
        personalizationSettingsResponse.aiRecommendationsEnabled( vo.isAiRecommendationsEnabled() );
        personalizationSettingsResponse.chatAutoOrderEnabled( vo.isChatAutoOrderEnabled() );
        personalizationSettingsResponse.chatLanguage( vo.getChatLanguage() );
        personalizationSettingsResponse.chatStyle( vo.getChatStyle() );
        personalizationSettingsResponse.dietaryWeight( vo.getDietaryWeight() );
        personalizationSettingsResponse.learningFromHistoryEnabled( vo.isLearningFromHistoryEnabled() );
        personalizationSettingsResponse.locationBasedPersonalization( vo.isLocationBasedPersonalization() );
        personalizationSettingsResponse.orderHistoryWeight( vo.getOrderHistoryWeight() );
        personalizationSettingsResponse.showPersonalizedBanners( vo.isShowPersonalizedBanners() );
        personalizationSettingsResponse.showQuickReorder( vo.isShowQuickReorder() );
        personalizationSettingsResponse.showSuggestions( vo.isShowSuggestions() );
        personalizationSettingsResponse.smartReorderEnabled( vo.isSmartReorderEnabled() );
        personalizationSettingsResponse.voiceOrderingEnabled( vo.isVoiceOrderingEnabled() );

        return personalizationSettingsResponse.build();
    }
}
