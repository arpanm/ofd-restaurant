package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * History tracking for Restaurant changes.
 * Maintains complete audit trail of all restaurant modifications.
 */
@Document(collection = "restaurant_history")
@CompoundIndexes({
    @CompoundIndex(name = "restaurant_field_time", def = "{'restaurantId': 1, 'fieldName': 1, 'changedAt': -1}"),
    @CompoundIndex(name = "restaurant_time", def = "{'restaurantId': 1, 'changedAt': -1}"),
    @CompoundIndex(name = "changed_by_time", def = "{'changedBy': 1, 'changedAt': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantHistory {
    /**
     * Unique history entry ID
     */
    @Id
    private String id;
    
    /**
     * Reference to restaurant
     */
    @Indexed
    private String restaurantId;
    
    /**
     * Name of the field that changed
     */
    private String fieldName;
    
    /**
     * Previous value (stored as Object for flexibility)
     */
    private Object oldValue;
    
    /**
     * New value
     */
    private Object newValue;
    
    /**
     * Type of change
     */
    private ChangeType changeType;
    
    /**
     * User ID who made the change
     */
    @Indexed
    private String changedBy;
    
    /**
     * Timestamp of change
     */
    @Indexed
    private Instant changedAt;
    
    /**
     * Optional reason for change
     */
    private String changeReason;
    
    /**
     * IP address from where change originated
     */
    private String sourceIp;
    
    /**
     * Browser/app user agent
     */
    private String userAgent;
}


