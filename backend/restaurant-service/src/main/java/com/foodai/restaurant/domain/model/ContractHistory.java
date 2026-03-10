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
 * History tracking for Contract changes.
 * Contract changes are critical and require special tracking with approval.
 */
@Document(collection = "contract_history")
@CompoundIndexes({
    @CompoundIndex(name = "contract_time", def = "{'restaurantId': 1, 'changedAt': -1}"),
    @CompoundIndex(name = "contract_id_time", def = "{'contractId': 1, 'changedAt': -1}"),
    @CompoundIndex(name = "requires_resigning_idx", def = "{'requiresResigning': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractHistory {
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
     * Reference to contract
     */
    @Indexed
    private String contractId;
    
    /**
     * Field that changed (e.g., "platformFee.percentageRate")
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
     * Reason for change (REQUIRED for contract changes)
     */
    private String changeReason;
    
    /**
     * Whether this change requires contract re-signing
     */
    private Boolean requiresResigning;
    
    /**
     * Operations manager who approved the change
     */
    private String approvedBy;
    
    /**
     * Approval timestamp
     */
    private Instant approvedAt;
}


