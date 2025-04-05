package com.travelapi.multidestination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Représente un itinéraire complet multi-destinations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "itineraries")
public class Itinerary {
    
    @Id
    private String id;
    
    private double totalPrice;
    private String totalDuration;
    private List<FlightLeg> legs;
    private List<City> cities;
    private BudgetBreakdown budgetBreakdown;
    private List<String> optimizationTips;
    private LocalDateTime createdAt;
    private String searchId; // Référence à la recherche d'origine
}
