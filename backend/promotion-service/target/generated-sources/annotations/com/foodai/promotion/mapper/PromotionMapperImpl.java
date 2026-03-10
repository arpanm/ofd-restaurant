package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.ApplicabilityVO;
import com.foodai.promotion.domain.model.DiscountVO;
import com.foodai.promotion.domain.model.Promotion;
import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.domain.model.ScheduleVO;
import com.foodai.promotion.domain.model.UsageLimitVO;
import com.foodai.promotion.dto.request.CreatePromotionRequest;
import com.foodai.promotion.dto.response.PromotionResponse;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-15T21:19:19+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class PromotionMapperImpl implements PromotionMapper {

    @Override
    public Promotion toEntity(CreatePromotionRequest request) {
        if ( request == null ) {
            return null;
        }

        Promotion.PromotionBuilder promotion = Promotion.builder();

        promotion.discount( toDiscountVO( request ) );
        promotion.schedule( toScheduleVO( request ) );
        promotion.usageLimit( toUsageLimitVO( request ) );
        promotion.applicability( toApplicabilityVO( request ) );
        promotion.name( request.getName() );
        promotion.description( request.getDescription() );
        promotion.type( request.getType() );
        promotion.restaurantId( request.getRestaurantId() );
        promotion.code( request.getCode() );
        promotion.terms( request.getTerms() );
        promotion.platformWide( request.isPlatformWide() );
        promotion.featured( request.isFeatured() );
        promotion.priority( request.getPriority() );
        promotion.bannerUrl( request.getBannerUrl() );

        promotion.deleted( false );
        promotion.status( PromotionStatus.DRAFT );

        return promotion.build();
    }

    @Override
    public PromotionResponse toResponse(Promotion entity) {
        if ( entity == null ) {
            return null;
        }

        PromotionResponse.PromotionResponseBuilder promotionResponse = PromotionResponse.builder();

        promotionResponse.discountValue( entityDiscountValue( entity ) );
        promotionResponse.minOrderValue( entityDiscountMinOrderValue( entity ) );
        promotionResponse.maxDiscount( entityDiscountMaxDiscount( entity ) );
        promotionResponse.startDate( entityScheduleStartDate( entity ) );
        promotionResponse.endDate( entityScheduleEndDate( entity ) );
        promotionResponse.startTime( entityScheduleStartTime( entity ) );
        promotionResponse.endTime( entityScheduleEndTime( entity ) );
        Set<DayOfWeek> daysOfWeek = entityScheduleDaysOfWeek( entity );
        Set<DayOfWeek> set = daysOfWeek;
        if ( set != null ) {
            promotionResponse.daysOfWeek( new LinkedHashSet<DayOfWeek>( set ) );
        }
        promotionResponse.totalUsageLimit( entityUsageLimitTotalLimit( entity ) );
        promotionResponse.perUserLimit( entityUsageLimitPerUserLimit( entity ) );
        promotionResponse.currentUsage( entityUsageLimitCurrentUsage( entity ) );
        promotionResponse.remainingUsage( getRemainingUsage( entity.getUsageLimit() ) );
        promotionResponse.usagePercentage( getUsagePercentage( entity.getUsageLimit() ) );
        Set<String> restaurantIds = entityApplicabilityRestaurantIds( entity );
        Set<String> set1 = restaurantIds;
        if ( set1 != null ) {
            promotionResponse.applicableRestaurants( new LinkedHashSet<String>( set1 ) );
        }
        Set<String> menuItemIds = entityApplicabilityMenuItemIds( entity );
        Set<String> set2 = menuItemIds;
        if ( set2 != null ) {
            promotionResponse.applicableMenuItems( new LinkedHashSet<String>( set2 ) );
        }
        Set<String> categories = entityApplicabilityCategories( entity );
        Set<String> set3 = categories;
        if ( set3 != null ) {
            promotionResponse.applicableCategories( new LinkedHashSet<String>( set3 ) );
        }
        promotionResponse.id( entity.getId() );
        promotionResponse.name( entity.getName() );
        promotionResponse.description( entity.getDescription() );
        promotionResponse.type( entity.getType() );
        promotionResponse.status( entity.getStatus() );
        promotionResponse.restaurantId( entity.getRestaurantId() );
        promotionResponse.code( entity.getCode() );
        promotionResponse.terms( entity.getTerms() );
        promotionResponse.platformWide( entity.isPlatformWide() );
        promotionResponse.featured( entity.isFeatured() );
        promotionResponse.priority( entity.getPriority() );
        promotionResponse.bannerUrl( entity.getBannerUrl() );
        promotionResponse.campaignId( entity.getCampaignId() );
        promotionResponse.createdAt( entity.getCreatedAt() );
        promotionResponse.updatedAt( entity.getUpdatedAt() );

        return promotionResponse.build();
    }

    private BigDecimal entityDiscountValue(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        DiscountVO discount = promotion.getDiscount();
        if ( discount == null ) {
            return null;
        }
        BigDecimal value = discount.getValue();
        if ( value == null ) {
            return null;
        }
        return value;
    }

    private BigDecimal entityDiscountMinOrderValue(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        DiscountVO discount = promotion.getDiscount();
        if ( discount == null ) {
            return null;
        }
        BigDecimal minOrderValue = discount.getMinOrderValue();
        if ( minOrderValue == null ) {
            return null;
        }
        return minOrderValue;
    }

    private BigDecimal entityDiscountMaxDiscount(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        DiscountVO discount = promotion.getDiscount();
        if ( discount == null ) {
            return null;
        }
        BigDecimal maxDiscount = discount.getMaxDiscount();
        if ( maxDiscount == null ) {
            return null;
        }
        return maxDiscount;
    }

    private Instant entityScheduleStartDate(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ScheduleVO schedule = promotion.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        Instant startDate = schedule.getStartDate();
        if ( startDate == null ) {
            return null;
        }
        return startDate;
    }

    private Instant entityScheduleEndDate(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ScheduleVO schedule = promotion.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        Instant endDate = schedule.getEndDate();
        if ( endDate == null ) {
            return null;
        }
        return endDate;
    }

    private LocalTime entityScheduleStartTime(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ScheduleVO schedule = promotion.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        LocalTime startTime = schedule.getStartTime();
        if ( startTime == null ) {
            return null;
        }
        return startTime;
    }

    private LocalTime entityScheduleEndTime(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ScheduleVO schedule = promotion.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        LocalTime endTime = schedule.getEndTime();
        if ( endTime == null ) {
            return null;
        }
        return endTime;
    }

    private Set<DayOfWeek> entityScheduleDaysOfWeek(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ScheduleVO schedule = promotion.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        Set<DayOfWeek> daysOfWeek = schedule.getDaysOfWeek();
        if ( daysOfWeek == null ) {
            return null;
        }
        return daysOfWeek;
    }

    private Integer entityUsageLimitTotalLimit(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        UsageLimitVO usageLimit = promotion.getUsageLimit();
        if ( usageLimit == null ) {
            return null;
        }
        Integer totalLimit = usageLimit.getTotalLimit();
        if ( totalLimit == null ) {
            return null;
        }
        return totalLimit;
    }

    private Integer entityUsageLimitPerUserLimit(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        UsageLimitVO usageLimit = promotion.getUsageLimit();
        if ( usageLimit == null ) {
            return null;
        }
        Integer perUserLimit = usageLimit.getPerUserLimit();
        if ( perUserLimit == null ) {
            return null;
        }
        return perUserLimit;
    }

    private int entityUsageLimitCurrentUsage(Promotion promotion) {
        if ( promotion == null ) {
            return 0;
        }
        UsageLimitVO usageLimit = promotion.getUsageLimit();
        if ( usageLimit == null ) {
            return 0;
        }
        int currentUsage = usageLimit.getCurrentUsage();
        return currentUsage;
    }

    private Set<String> entityApplicabilityRestaurantIds(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ApplicabilityVO applicability = promotion.getApplicability();
        if ( applicability == null ) {
            return null;
        }
        Set<String> restaurantIds = applicability.getRestaurantIds();
        if ( restaurantIds == null ) {
            return null;
        }
        return restaurantIds;
    }

    private Set<String> entityApplicabilityMenuItemIds(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ApplicabilityVO applicability = promotion.getApplicability();
        if ( applicability == null ) {
            return null;
        }
        Set<String> menuItemIds = applicability.getMenuItemIds();
        if ( menuItemIds == null ) {
            return null;
        }
        return menuItemIds;
    }

    private Set<String> entityApplicabilityCategories(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }
        ApplicabilityVO applicability = promotion.getApplicability();
        if ( applicability == null ) {
            return null;
        }
        Set<String> categories = applicability.getCategories();
        if ( categories == null ) {
            return null;
        }
        return categories;
    }
}
