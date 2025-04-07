//package com.travelapi.multidestination.service.external;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.util.*;
//
///**
// * Service pour obtenir des informations sur les villes
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class CityDataService {
//
//    private final WebClient.Builder webClientBuilder;
//
//    @Value("${api.citydata.baseUrl:https://api.example.com/citydata}")
//    private String baseUrl;
//
//    /**
//     * Obtient le nom complet d'une ville à partir de son code
//     */
//    public String getCityName(String cityCode) {
//        log.info("Récupération du nom de la ville pour le code {}", cityCode);
//
//        Map<String, String> cityNames = getCityNames();
//        return cityNames.getOrDefault(cityCode, "Unknown City");
//    }
//
//    /**
//     * Obtient le pays d'une ville à partir de son code
//     */
//    public String getCityCountry(String cityCode) {
//        log.info("Récupération du pays pour la ville {}", cityCode);
//
//        Map<String, String> cityCountries = getCityCountries();
//        return cityCountries.getOrDefault(cityCode, "Unknown Country");
//    }
//
//    /**
//     * Obtient les points d'intérêt d'une ville
//     */
//    public List<String> getCityHighlights(String cityCode) {
//        log.info("Récupération des points d'intérêt pour la ville {}", cityCode);
//
//        Map<String, List<String>> cityHighlights = getCityHighlights();
//        return cityHighlights.getOrDefault(cityCode, Collections.emptyList());
//    }
//
//    /**
//     * Obtient les villes par continent
//     */
//    public List<String> getCitiesByContinent(String continent) {
//        log.info("Récupération des villes pour le continent {}", continent);
//
//        Map<String, List<String>> continentCities = getContinentCities();
//        return continentCities.getOrDefault(continent, Collections.emptyList());
//    }
//
//    /**
//     * Obtient les villes par intérêt
//     */
//    public List<String> getCitiesByInterest(String interest) {
//        log.info("Récupération des villes pour l'intérêt {}", interest);
//
//        Map<String, List<String>> interestCities = getInterestCities();
//        return interestCities.getOrDefault(interest, Collections.emptyList());
//    }
//
//    /**
//     * Obtient les villes recommandées en fonction des préférences
//     */
//    public List<String> getRecommendedCities(List<String> continents, List<String> interests) {
//        log.info("Récupération des villes recommandées pour continents {} et intérêts {}", continents, interests);
//
//        Set<String> recommendedCities = new HashSet<>();
//
//        // Ajouter toutes les villes des continents spécifiés
//        if (continents != null && !continents.isEmpty()) {
//            for (String continent : continents) {
//                recommendedCities.addAll(getCitiesByContinent(continent));
//            }
//        } else {
//            // Si aucun continent n'est spécifié, inclure toutes les villes
//            for (List<String> cities : getContinentCities().values()) {
//                recommendedCities.addAll(cities);
//            }
//        }
//
//        // Filtrer par intérêts si spécifiés
//        if (interests != null && !interests.isEmpty()) {
//            Set<String> interestCities = new HashSet<>();
//            for (String interest : interests) {
//                interestCities.addAll(getCitiesByInterest(interest));
//            }
//
//            // Conserver uniquement les villes qui correspondent à la fois aux continents et aux intérêts
//            recommendedCities.retainAll(interestCities);
//        }
//
//        return new ArrayList<>(recommendedCities);
//    }
//
//    /**
//     * Mapping entre les codes de villes et leurs noms
//     */
//    private Map<String, String> getCityNames() {
//        Map<String, String> cityNames = new HashMap<>();
//
//        cityNames.put("BCN", "Barcelona");
//        cityNames.put("MAD", "Madrid");
//        cityNames.put("LIS", "Lisbon");
//        cityNames.put("VAL", "Valencia");
//        cityNames.put("POR", "Porto");
//        cityNames.put("PAR", "Paris");
//        cityNames.put("ROM", "Rome");
//        cityNames.put("AMS", "Amsterdam");
//        cityNames.put("BER", "Berlin");
//        cityNames.put("SVQ", "Seville");
//        cityNames.put("MIL", "Milan");
//        cityNames.put("ATH", "Athens");
//        cityNames.put("TYO", "Tokyo");
//        cityNames.put("BKK", "Bangkok");
//        cityNames.put("SIN", "Singapore");
//        cityNames.put("HKG", "Hong Kong");
//        cityNames.put("BJS", "Beijing");
//        cityNames.put("SEL", "Seoul");
//        cityNames.put("NYC", "New York");
//        cityNames.put("LAX", "Los Angeles");
//        cityNames.put("MIA", "Miami");
//        cityNames.put("MEX", "Mexico City");
//        cityNames.put("RIO", "Rio de Janeiro");
//        cityNames.put("BOG", "Bogota");
//
//        return cityNames;
//    }
//
//    /**
//     * Mapping entre les codes de villes et leurs pays
//     */
//    private Map<String, String> getCityCountries() {
//        Map<String, String> cityCountries = new HashMap<>();
//
//        cityCountries.put("BCN", "Spain");
//        cityCountries.put("MAD", "Spain");
//        cityCountries.put("LIS", "Portugal");
//        cityCountries.put("VAL", "Spain");
//        cityCountries.put("POR", "Portugal");
//        cityCountries.put("PAR", "France");
//        cityCountries.put("ROM", "Italy");
//        cityCountries.put("AMS", "Netherlands");
//        cityCountries.put("BER", "Germany");
//        cityCountries.put("SVQ", "Spain");
//        cityCountries.put("MIL", "Italy");
//        cityCountries.put("ATH", "Greece");
//        cityCountries.put("TYO", "Japan");
//        cityCountries.put("BKK", "Thailand");
//        cityCountries.put("SIN", "Singapore");
//        cityCountries.put("HKG", "China");
//        cityCountries.put("BJS", "China");
//        cityCountries.put("SEL", "South Korea");
//        cityCountries.put("NYC", "United States");
//        cityCountries.put("LAX", "United States");
//        cityCountries.put("MIA", "United States");
//        cityCountries.put("MEX", "Mexico");
//        cityCountries.put("RIO", "Brazil");
//        cityCountries.put("BOG", "Colombia");
//
//        return cityCountries;
//    }
//
//    /**
//     * Mapping entre les codes de villes et leurs points d'intérêt
//     */
//    private Map<String, List<String>> getCityHighlights() {
//        Map<String, List<String>> cityHighlights = new HashMap<>();
//
//        cityHighlights.put("BCN", Arrays.asList("Sagrada Familia", "Park Güell", "La Rambla", "Barceloneta Beach"));
//        cityHighlights.put("MAD", Arrays.asList("Prado Museum", "Royal Palace", "Retiro Park", "Plaza Mayor"));
//        cityHighlights.put("LIS", Arrays.asList("Belém Tower", "Jerónimos Monastery", "Alfama District", "Time Out Market"));
//        cityHighlights.put("VAL", Arrays.asList("City of Arts and Sciences", "Valencia Cathedral", "Malvarrosa Beach", "Central Market"));
//        cityHighlights.put("POR", Arrays.asList("Ribeira District", "Dom Luís I Bridge", "Porto Cathedral", "Wine Cellars"));
//        cityHighlights.put("PAR", Arrays.asList("Eiffel Tower", "Louvre Museum", "Notre-Dame Cathedral", "Montmartre"));
//        cityHighlights.put("ROM", Arrays.asList("Colosseum", "Vatican Museums", "Trevi Fountain", "Roman Forum"));
//        cityHighlights.put("AMS", Arrays.asList("Anne Frank House", "Van Gogh Museum", "Canal Cruise", "Vondelpark"));
//        cityHighlights.put("BER", Arrays.asList("Brandenburg Gate", "Berlin Wall Memorial", "Museum Island", "Reichstag Building"));
//        cityHighlights.put("SVQ", Arrays.asList("Alcázar Palace", "Seville Cathedral", "Plaza de España", "Barrio Santa Cruz"));
//        cityHighlights.put("MIL", Arrays.asList("Milan Cathedral", "Galleria Vittorio Emanuele II", "Sforza Castle", "The Last Supper"));
//        cityHighlights.put("ATH", Arrays.asList("Acropolis", "Parthenon", "National Archaeological Museum", "Plaka District"));
//
//        return cityHighlights;
//    }
//
//    /**
//     * Mapping entre les continents et leurs villes
//     */
//    private Map<String, List<String>> getContinentCities() {
//        Map<String, List<String>> continentCities = new HashMap<>();
//
//        continentCities.put("Europe", Arrays.asList("BCN", "MAD", "LIS", "VAL", "POR", "PAR", "ROM", "AMS", "BER", "SVQ", "MIL", "ATH"));
//        continentCities.put("Asia", Arrays.asList("TYO", "BKK", "SIN", "HKG", "BJS", "SEL"));
//        continentCities.put("America", Arrays.asList("NYC", "LAX", "MIA", "MEX", "RIO", "BOG"));
//
//        return continentCities;
//    }
//
//    /**
//     * Mapping entre les intérêts et leurs villes
//     */
//    private Map<String, List<String>> getInterestCities() {
//        Map<String, List<String>> interestCities = new HashMap<>();
//
//        interestCities.put("culture", Arrays.asList("BCN", "MAD", "PAR", "ROM", "ATH", "BER", "TYO", "NYC", "VAL", "LIS"));
//        interestCities.put("beach", Arrays.asList("BCN", "LIS", "VAL", "BKK", "MIA", "RIO", "SVQ", "ATH"));
//        interestCities.put("food", Arrays.asList("BCN", "MAD", "PAR", "VAL", "ROM", "TYO", "BKK", "MEX", "MIL", "LIS", "POR"));
//        interestCities.put("nature", Arrays.asList("POR", "SVQ", "BER", "BJS", "RIO", "BOG", "AMS"));
//        interestCities.put("shopping", Arrays.asList("PAR", "MIL", "BER", "HKG", "NYC", "LAX", "MAD", "BCN"));
//        interestCities.put("history", Arrays.asList("ROM", "ATH", "BER", "BCN", "MAD", "PAR", "SVQ", "LIS"));
//        interestCities.put("nightlife", Arrays.asList("BCN", "MAD", "PAR", "BER", "NYC", "MIA", "BKK", "RIO"));
//
//        return interestCities;
//    }
//}