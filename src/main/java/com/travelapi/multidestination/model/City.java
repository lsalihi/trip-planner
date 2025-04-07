package com.travelapi.multidestination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Représente une ville dans un itinéraire
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class City {
    
    private String code;
    private String name;
    private String country;
    private String stayDuration;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private List<String> highlights;
    private WeatherForecast weatherForecast;
}

