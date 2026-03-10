package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object representing sentiment analysis result.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentVO {

    /**
     * Overall sentiment (positive, negative, neutral)
     */
    private String sentiment;

    /**
     * Sentiment score (-1.0 to 1.0)
     */
    private Double score;

    /**
     * Confidence level (0.0 to 1.0)
     */
    private Double confidence;

    /**
     * Key phrases detected
     */
    private String[] keyPhrases;

    /**
     * Detected emotions
     */
    private String[] emotions;
}

