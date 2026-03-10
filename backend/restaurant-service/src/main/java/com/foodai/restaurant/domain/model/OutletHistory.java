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
 * History tracking for Outlet changes.
 * Maintains complete audit trail of all outlet modifications.
 */
@Document(collection = "outlet_history")
@CompoundIndexes({
    @CompoundIndex(name = "outlet_field_time", def = "{'outletId': 1, 'fieldName': 1, 'changedAt': -1}"),
    @CompoundIndex(name = "outlet_time", def = "{'outletId': 1, 'changedAt': -1}"),
    @CompoundIndex(name = "restaurant_outlet_time", def = "{'restaurantId': 1, 'outletId': 1, 'changedAt': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutletHistory {
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
     * Reference to outlet
     */
    @Indexed
    private String outletId;
    
    /**
     * Name of the field that changed
     */
    private String fieldName;
    
    /**
     * Previous value
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


