//package com.travelapi.multidestination.service.external;
//
//import com.travelapi.multidestination.model.WeatherForecast;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.time.LocalDate;
//import java.time.Month;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//
///**
// * Service pour obtenir les prévisions météo pour une ville donnée
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class WeatherService {
//
//    private final WebClient.Builder webClientBuilder;
//
//    @Value("${api.weather.baseUrl}")
//    private String baseUrl;
//
//    @Value("${api.weather.apiKey}")
//    private String apiKey;
//
//    /**
//     * Obtient les prévisions météo pour une ville à une date donnée
//     */
//    public WeatherForecast getWeatherForecast(String cityCode, LocalDate date) {
//        log.info("Récupération des prévisions météo pour {} le {}", cityCode, date);
//
//        try {
//            // Dans une implémentation réelle, nous appellerions l'API météo
//            // Pour cet exemple, nous simulons une réponse
//
//            return simulateWeatherForecast(cityCode, date);
//
//        } catch (Exception e) {
//            log.error("Erreur lors de la récupération des prévisions météo", e);
//
//            // Retourner des données par défaut en cas d'erreur
//            return WeatherForecast.builder()
//                    .averageTemp(25)
//                    .condition("Unknown")
//                    .build();
//        }
//    }
//
//    /**
//     * Simule des prévisions météo pour une démo
//     */
//    private WeatherForecast simulateWeatherForecast(String cityCode, LocalDate date) {
//        // Températures moyennes par ville et par saison
//        Map<String, Map<String, Integer>> tempBySeason = initializeTemperatureData();
//
//        // Conditions météo possibles par saison
//        Map<String, String[]> conditionsBySeason = initializeConditionsData();
//
//        // Déterminer la saison en fonction de la date
//        String season = getSeason(date);
//
//        // Récupérer la température moyenne pour la ville et la saison
//        int baseTemp = 25; // Valeur par défaut
//
//        if (tempBySeason.containsKey(cityCode)) {
//            Map<String, Integer> cityTemps = tempBySeason.get(cityCode);
//            baseTemp = cityTemps.getOrDefault(season, baseTemp);
//        }
//
//        // Ajouter une petite variation aléatoire
//        Random random = new Random(date.toEpochDay() + cityCode.hashCode()); // Rendre prévisible pour une même ville/date
//        int tempVariation = random.nextInt(5) - 2;  // Entre -2 et +2 degrés
//        int finalTemp = baseTemp + tempVariation;
//
//        // Sélectionner une condition météo en fonction de la saison
//        String[] seasonConditions = conditionsBySeason.getOrDefault(season,
//                new String[]{"Sunny", "Mostly Sunny", "Partly Cloudy"});
//
//        // Les villes plus chaudes ont plus de chances d'être ensoleillées
//        int conditionIndex;
//        if (finalTemp > 30) {
//            conditionIndex = random.nextInt(Math.min(2, seasonConditions.length));  // Principalement ensoleillé
//        } else if (finalTemp > 25) {
//            conditionIndex = random.nextInt(Math.min(3, seasonConditions.length));  // Ensoleillé ou partiellement nuageux
//        } else {
//            conditionIndex = random.nextInt(seasonConditions.length);  // Toutes conditions possibles
//        }
//
//        String condition = seasonConditions[conditionIndex];
//
//        return WeatherForecast.builder()
//                .averageTemp(finalTemp)
//                .condition(condition)
//                .build();
//    }
//
//    /**
//     * Détermine la saison en fonction de la date (pour l'hémisphère nord)
//     */
//    private String getSeason(LocalDate date) {
//        Month month = date.getMonth();
//
//        if (month == Month.DECEMBER || month == Month.JANUARY || month == Month.FEBRUARY) {
//            return "WINTER";
//        } else if (month == Month.MARCH || month == Month.APRIL || month == Month.MAY) {
//            return "SPRING";
//        } else if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST) {
//            return "SUMMER";
//        } else {
//            return "AUTUMN";
//        }
//    }
//
//    /**
//     * Initialise les données de température par ville et par saison
//     */
//    private Map<String, Map<String, Integer>> initializeTemperatureData() {
//        Map<String, Map<String, Integer>> tempBySeason = new HashMap<>();
//
//        // Barcelona
//        Map<String, Integer> bcnTemps = new HashMap<>();
//        bcnTemps.put("WINTER", 12);
//        bcnTemps.put("SPRING", 18);
//        bcnTemps.put("SUMMER", 28);
//        bcnTemps.put("AUTUMN", 20);
//        tempBySeason.put("BCN", bcnTemps);
//
//        // Madrid
//        Map<String, Integer> madTemps = new HashMap<>();
//        madTemps.put("WINTER", 8);
//        madTemps.put("SPRING", 16);
//        madTemps.put("SUMMER", 32);
//        madTemps.put("AUTUMN", 18);
//        tempBySeason.put("MAD", madTemps);
//
//        // Lisbon
//        Map<String, Integer> lisTemps = new HashMap<>();
//        lisTemps.put("WINTER", 13);
//        lisTemps.put("SPRING", 17);
//        lisTemps.put("SUMMER", 26);
//        lisTemps.put("AUTUMN", 19);
//        tempBySeason.put("LIS", lisTemps);
//
//        // Valencia
//        Map<String, Integer> valTemps = new HashMap<>();
//        valTemps.put("WINTER", 14);
//        valTemps.put("SPRING", 19);
//        valTemps.put("SUMMER", 30);
//        valTemps.put("AUTUMN", 22);
//        tempBySeason.put("VAL", valTemps);
//
//        // Porto
//        Map<String, Integer> porTemps = new HashMap<>();
//        porTemps.put("WINTER", 11);
//        porTemps.put("SPRING", 15);
//        porTemps.put("SUMMER", 24);
//        porTemps.put("AUTUMN", 17);
//        tempBySeason.put("POR", porTemps);
//
//        // Seville
//        Map<String, Integer> svqTemps = new HashMap<>();
//        svqTemps.put("WINTER", 13);
//        svqTemps.put("SPRING", 20);
//        svqTemps.put("SUMMER", 35);
//        svqTemps.put("AUTUMN", 23);
//        tempBySeason.put("SVQ", svqTemps);
//
//        // Rome
//        Map<String, Integer> romTemps = new HashMap<>();
//        romTemps.put("WINTER", 10);
//        romTemps.put("SPRING", 18);
//        romTemps.put("SUMMER", 31);
//        romTemps.put("AUTUMN", 20);
//        tempBySeason.put("ROM", romTemps);
//
//        // Paris
//        Map<String, Integer> parTemps = new HashMap<>();
//        parTemps.put("WINTER", 5);
//        parTemps.put("SPRING", 13);
//        parTemps.put("SUMMER", 25);
//        parTemps.put("AUTUMN", 14);
//        tempBySeason.put("PAR", parTemps);
//
//        // Amsterdam
//        Map<String, Integer> amsTemps = new HashMap<>();
//        amsTemps.put("WINTER", 3);
//        amsTemps.put("SPRING", 10);
//        amsTemps.put("SUMMER", 22);
//        amsTemps.put("AUTUMN", 12);
//        tempBySeason.put("AMS", amsTemps);
//
//        // Berlin
//        Map<String, Integer> berTemps = new HashMap<>();
//        berTemps.put("WINTER", 1);
//        berTemps.put("SPRING", 12);
//        berTemps.put("SUMMER", 24);
//        berTemps.put("AUTUMN", 13);
//        tempBySeason.put("BER", berTemps);
//
//        // Milan
//        Map<String, Integer> milTemps = new HashMap<>();
//        milTemps.put("WINTER", 4);
//        milTemps.put("SPRING", 15);
//        milTemps.put("SUMMER", 29);
//        milTemps.put("AUTUMN", 16);
//        tempBySeason.put("MIL", milTemps);
//
//        // Athens
//        Map<String, Integer> athTemps = new HashMap<>();
//        athTemps.put("WINTER", 12);
//        athTemps.put("SPRING", 19);
//        athTemps.put("SUMMER", 33);
//        athTemps.put("AUTUMN", 22);
//        tempBySeason.put("ATH", athTemps);
//
//        return tempBySeason;
//    }
//
//    /**
//     * Initialise les conditions météo possibles par saison
//     */
//    private Map<String, String[]> initializeConditionsData() {
//        Map<String, String[]> conditionsBySeason = new HashMap<>();
//
//        conditionsBySeason.put("WINTER", new String[]{"Cloudy", "Light Rain", "Heavy Rain", "Snow", "Partly Cloudy"});
//        conditionsBySeason.put("SPRING", new String[]{"Partly Cloudy", "Light Rain", "Mostly Sunny", "Sunny", "Windy"});
//        conditionsBySeason.put("SUMMER", new String[]{"Sunny", "Mostly Sunny", "Partly Cloudy", "Light Rain", "Thunderstorm"});
//        conditionsBySeason.put("AUTUMN", new String[]{"Partly Cloudy", "Cloudy", "Light Rain", "Mostly Sunny", "Fog"});
//
//        return conditionsBySeason;
//    }
//}