package com.travelapi.multidestination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente la répartition du budget pour un itinéraire
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetBreakdown {
    
    private double flights;
    private double estimatedAccommodation;
    private double estimatedFood;
    private double total;
    private double remainingBudget;
}
