package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.Campaign;
import com.foodai.promotion.domain.model.CampaignFrequency;
import com.foodai.promotion.domain.model.CampaignStatus;
import com.foodai.promotion.domain.model.ChannelType;
import com.foodai.promotion.domain.model.ScheduleVO;
import com.foodai.promotion.dto.request.CreateCampaignRequest;
import com.foodai.promotion.dto.response.CampaignResponse;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-15T21:19:20+0530",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Eclipse Adoptium)"
)
@Component
public class CampaignMapperImpl implements CampaignMapper {

    @Override
    public Campaign toEntity(CreateCampaignRequest request) {
        if ( request == null ) {
            return null;
        }

        Campaign.CampaignBuilder campaign = Campaign.builder();

        campaign.schedule( toScheduleVO( request ) );
        campaign.creatives( toCreatives( request.getCreatives() ) );
        campaign.name( request.getName() );
        campaign.description( request.getDescription() );
        campaign.restaurantId( request.getRestaurantId() );
        Set<String> set = request.getSegmentIds();
        if ( set != null ) {
            campaign.segmentIds( new LinkedHashSet<String>( set ) );
        }
        Set<String> set1 = request.getTargetSegmentIds();
        if ( set1 != null ) {
            campaign.targetSegmentIds( new LinkedHashSet<String>( set1 ) );
        }
        Set<ChannelType> set2 = request.getChannels();
        if ( set2 != null ) {
            campaign.channels( new LinkedHashSet<ChannelType>( set2 ) );
        }
        campaign.type( request.getType() );
        Map<String, String> map = request.getContent();
        if ( map != null ) {
            campaign.content( new LinkedHashMap<String, String>( map ) );
        }
        Set<String> set3 = request.getPromotionIds();
        if ( set3 != null ) {
            campaign.promotionIds( new LinkedHashSet<String>( set3 ) );
        }
        Set<String> set4 = request.getLinkedPromotionIds();
        if ( set4 != null ) {
            campaign.linkedPromotionIds( new LinkedHashSet<String>( set4 ) );
        }
        Set<String> set5 = request.getLinkedCouponIds();
        if ( set5 != null ) {
            campaign.linkedCouponIds( new LinkedHashSet<String>( set5 ) );
        }
        campaign.budget( request.getBudget() );
        campaign.trackingEnabled( request.isTrackingEnabled() );
        campaign.abTestEnabled( request.isAbTestEnabled() );
        campaign.variantPercentage( request.getVariantPercentage() );

        campaign.deleted( false );
        campaign.status( CampaignStatus.DRAFT );
        campaign.aiSuggested( false );

        return campaign.build();
    }

    @Override
    public CampaignResponse toResponse(Campaign entity) {
        if ( entity == null ) {
            return null;
        }

        CampaignResponse.CampaignResponseBuilder campaignResponse = CampaignResponse.builder();

        campaignResponse.startDate( entityScheduleStartDate( entity ) );
        campaignResponse.endDate( entityScheduleEndDate( entity ) );
        campaignResponse.startTime( entityScheduleStartTime( entity ) );
        campaignResponse.frequency( entityScheduleFrequency( entity ) );
        Set<DayOfWeek> daysOfWeek = entityScheduleDaysOfWeek( entity );
        Set<DayOfWeek> set = daysOfWeek;
        if ( set != null ) {
            campaignResponse.daysOfWeek( new LinkedHashSet<DayOfWeek>( set ) );
        }
        campaignResponse.creatives( toCreativeResponses( entity.getCreatives() ) );
        campaignResponse.performance( toPerformanceResponse( entity.getPerformance() ) );
        campaignResponse.id( entity.getId() );
        campaignResponse.name( entity.getName() );
        campaignResponse.description( entity.getDescription() );
        campaignResponse.status( entity.getStatus() );
        campaignResponse.restaurantId( entity.getRestaurantId() );
        Set<String> set1 = entity.getSegmentIds();
        if ( set1 != null ) {
            campaignResponse.segmentIds( new LinkedHashSet<String>( set1 ) );
        }
        Set<ChannelType> set2 = entity.getChannels();
        if ( set2 != null ) {
            campaignResponse.channels( new LinkedHashSet<ChannelType>( set2 ) );
        }
        Set<String> set3 = entity.getPromotionIds();
        if ( set3 != null ) {
            campaignResponse.promotionIds( new LinkedHashSet<String>( set3 ) );
        }
        campaignResponse.budget( entity.getBudget() );
        campaignResponse.spent( entity.getSpent() );
        campaignResponse.aiSuggested( entity.isAiSuggested() );
        campaignResponse.createdAt( entity.getCreatedAt() );
        campaignResponse.updatedAt( entity.getUpdatedAt() );

        return campaignResponse.build();
    }

    private Instant entityScheduleStartDate(Campaign campaign) {
        if ( campaign == null ) {
            return null;
        }
        ScheduleVO schedule = campaign.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        Instant startDate = schedule.getStartDate();
        if ( startDate == null ) {
            return null;
        }
        return startDate;
    }

    private Instant entityScheduleEndDate(Campaign campaign) {
        if ( campaign == null ) {
            return null;
        }
        ScheduleVO schedule = campaign.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        Instant endDate = schedule.getEndDate();
        if ( endDate == null ) {
            return null;
        }
        return endDate;
    }

    private LocalTime entityScheduleStartTime(Campaign campaign) {
        if ( campaign == null ) {
            return null;
        }
        ScheduleVO schedule = campaign.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        LocalTime startTime = schedule.getStartTime();
        if ( startTime == null ) {
            return null;
        }
        return startTime;
    }

    private CampaignFrequency entityScheduleFrequency(Campaign campaign) {
        if ( campaign == null ) {
            return null;
        }
        ScheduleVO schedule = campaign.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        CampaignFrequency frequency = schedule.getFrequency();
        if ( frequency == null ) {
            return null;
        }
        return frequency;
    }

    private Set<DayOfWeek> entityScheduleDaysOfWeek(Campaign campaign) {
        if ( campaign == null ) {
            return null;
        }
        ScheduleVO schedule = campaign.getSchedule();
        if ( schedule == null ) {
            return null;
        }
        Set<DayOfWeek> daysOfWeek = schedule.getDaysOfWeek();
        if ( daysOfWeek == null ) {
            return null;
        }
        return daysOfWeek;
    }
}
