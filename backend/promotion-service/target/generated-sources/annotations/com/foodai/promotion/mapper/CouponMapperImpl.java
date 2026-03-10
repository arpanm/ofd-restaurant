package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.Coupon;
import com.foodai.promotion.dto.request.CreateCouponRequest;
import com.foodai.promotion.dto.response.CouponResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-15T21:19:20+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class CouponMapperImpl implements CouponMapper {

    @Override
    public Coupon toEntity(CreateCouponRequest request) {
        if ( request == null ) {
            return null;
        }

        Coupon.CouponBuilder coupon = Coupon.builder();

        coupon.code( toUpperCase( request.getCode() ) );
        coupon.name( request.getName() );
        coupon.description( request.getDescription() );
        coupon.type( request.getType() );
        coupon.value( request.getValue() );
        coupon.discountValue( request.getDiscountValue() );
        coupon.minOrderValue( request.getMinOrderValue() );
        coupon.maxDiscount( request.getMaxDiscount() );
        coupon.validFrom( request.getValidFrom() );
        coupon.validUntil( request.getValidUntil() );
        coupon.totalLimit( request.getTotalLimit() );
        coupon.perUserLimit( request.getPerUserLimit() );
        coupon.promotionId( request.getPromotionId() );
        coupon.restaurantId( request.getRestaurantId() );
        coupon.firstOrderOnly( request.isFirstOrderOnly() );
        coupon.newUsersOnly( request.isNewUsersOnly() );
        Set<String> set = request.getApplicableRestaurants();
        if ( set != null ) {
            coupon.applicableRestaurants( new LinkedHashSet<String>( set ) );
        }
        Set<String> set1 = request.getApplicableCategories();
        if ( set1 != null ) {
            coupon.applicableCategories( new LinkedHashSet<String>( set1 ) );
        }

        coupon.deleted( false );
        coupon.active( true );
        coupon.currentUsage( 0 );

        return coupon.build();
    }

    @Override
    public CouponResponse toResponse(Coupon entity) {
        if ( entity == null ) {
            return null;
        }

        CouponResponse.CouponResponseBuilder couponResponse = CouponResponse.builder();

        couponResponse.remainingUsage( getRemainingUsage( entity ) );
        couponResponse.expired( isExpired( entity ) );
        couponResponse.id( entity.getId() );
        couponResponse.code( entity.getCode() );
        couponResponse.name( entity.getName() );
        couponResponse.description( entity.getDescription() );
        couponResponse.type( entity.getType() );
        couponResponse.value( entity.getValue() );
        couponResponse.discountValue( entity.getDiscountValue() );
        couponResponse.minOrderValue( entity.getMinOrderValue() );
        couponResponse.maxDiscount( entity.getMaxDiscount() );
        couponResponse.validFrom( entity.getValidFrom() );
        couponResponse.validUntil( entity.getValidUntil() );
        couponResponse.totalLimit( entity.getTotalLimit() );
        couponResponse.perUserLimit( entity.getPerUserLimit() );
        couponResponse.currentUsage( entity.getCurrentUsage() );
        couponResponse.restaurantId( entity.getRestaurantId() );
        couponResponse.promotionId( entity.getPromotionId() );
        couponResponse.active( entity.isActive() );
        couponResponse.firstOrderOnly( entity.isFirstOrderOnly() );
        couponResponse.newUsersOnly( entity.isNewUsersOnly() );
        Set<String> set = entity.getApplicableRestaurants();
        if ( set != null ) {
            couponResponse.applicableRestaurants( new LinkedHashSet<String>( set ) );
        }
        Set<String> set1 = entity.getApplicableCategories();
        if ( set1 != null ) {
            couponResponse.applicableCategories( new LinkedHashSet<String>( set1 ) );
        }
        couponResponse.createdAt( entity.getCreatedAt() );
        couponResponse.updatedAt( entity.getUpdatedAt() );

        return couponResponse.build();
    }
}
