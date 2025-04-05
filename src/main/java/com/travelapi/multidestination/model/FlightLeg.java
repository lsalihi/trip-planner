package com.travelapi.multidestination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Représente un segment de vol dans un itinéraire
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlightLeg {
    
    private String from;
    private String to;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private LocalDate arrivalDate;
    private LocalTime arrivalTime;
    private String airline;
    private String flightNumber;
    private double price;
    private String duration;
}
