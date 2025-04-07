package com.travelapi.multidestination.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Représente les prévisions météorologiques pour une ville
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecast {
    private int averageTemp;
    private String condition;
}
