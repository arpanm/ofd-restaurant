package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.CustomerSegment;
import com.foodai.promotion.dto.request.CreateSegmentRequest;
import com.foodai.promotion.dto.response.CustomerSegmentResponse;
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
public class CustomerSegmentMapperImpl implements CustomerSegmentMapper {

    @Override
    public CustomerSegment toEntity(CreateSegmentRequest request) {
        if ( request == null ) {
            return null;
        }

        CustomerSegment.CustomerSegmentBuilder customerSegment = CustomerSegment.builder();

        customerSegment.criteria( toCriteriaVO( request ) );
        customerSegment.name( request.getName() );
        customerSegment.description( request.getDescription() );
        customerSegment.restaurantId( request.getRestaurantId() );
        customerSegment.dynamic( request.isDynamic() );
        Set<String> set = request.getStaticUserIds();
        if ( set != null ) {
            customerSegment.staticUserIds( new LinkedHashSet<String>( set ) );
        }

        customerSegment.deleted( false );
        customerSegment.active( true );
        customerSegment.custom( true );
        customerSegment.aiSuggested( false );
        customerSegment.customerCount( 0 );

        return customerSegment.build();
    }

    @Override
    public CustomerSegmentResponse toResponse(CustomerSegment entity) {
        if ( entity == null ) {
            return null;
        }

        CustomerSegmentResponse.CustomerSegmentResponseBuilder customerSegmentResponse = CustomerSegmentResponse.builder();

        customerSegmentResponse.criteria( toCriteriaResponse( entity.getCriteria() ) );
        customerSegmentResponse.id( entity.getId() );
        customerSegmentResponse.name( entity.getName() );
        customerSegmentResponse.description( entity.getDescription() );
        customerSegmentResponse.restaurantId( entity.getRestaurantId() );
        customerSegmentResponse.customerCount( entity.getCustomerCount() );
        customerSegmentResponse.custom( entity.isCustom() );
        customerSegmentResponse.aiSuggested( entity.isAiSuggested() );
        customerSegmentResponse.active( entity.isActive() );
        customerSegmentResponse.lastRefreshed( entity.getLastRefreshed() );
        customerSegmentResponse.createdAt( entity.getCreatedAt() );
        customerSegmentResponse.updatedAt( entity.getUpdatedAt() );

        return customerSegmentResponse.build();
    }
}
