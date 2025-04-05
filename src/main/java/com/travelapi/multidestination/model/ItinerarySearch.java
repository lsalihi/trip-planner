package com.travelapi.multidestination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

/**
 * Représente une requête de recherche d'itinéraires multi-destinations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "itinerary_searches")
public class ItinerarySearch {
    
    @Id
    private String id;
    
    private String origin;
    private LocalDate departureDate;
    private LocalDate returnDate;
    private double budget;
    private int numberOfCities;
    private int minDaysPerCity;
    private int maxDaysPerCity;
    private List<String> continentPreferences;
    private List<String> interestPreferences;
    private List<String> excludedDestinations;
    private LocalDate createdAt;
}
