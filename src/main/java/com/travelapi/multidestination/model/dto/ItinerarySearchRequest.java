package com.travelapi.multidestination.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour la requête de recherche d'itinéraires
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarySearchRequest {
    
    @NotBlank(message = "L'origine est obligatoire")
    @Size(min = 3, max = 3, message = "Le code IATA de l'origine doit contenir 3 caractères")
    private String origin;
    
    @NotNull(message = "La date de départ est obligatoire")
    private LocalDate departureDate;
    
    @NotNull(message = "La date de retour est obligatoire")
    private LocalDate returnDate;
    
    @NotNull(message = "Le budget est obligatoire")
    @Min(value = 1, message = "Le budget doit être supérieur à 0")
    private Double budget;
    
    @NotNull(message = "Le nombre de villes est obligatoire")
    @Min(value = 1, message = "Le nombre de villes doit être au moins 1")
    private Integer numberOfCities;
    
    @Min(value = 1, message = "Le nombre minimum de jours par ville doit être au moins 1")
    private Integer minDaysPerCity;
    
    @Min(value = 1, message = "Le nombre maximum de jours par ville doit être au moins 1")
    private Integer maxDaysPerCity;
    
    private Preferences preferences;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Preferences {
        private List<String> continent;
        private List<String> interests;
        private List<String> excludedDestinations;
    }
}
