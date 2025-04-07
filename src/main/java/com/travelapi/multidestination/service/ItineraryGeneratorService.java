package com.travelapi.multidestination.service;

import com.travelapi.multidestination.model.*;
import com.travelapi.multidestination.model.dto.ItinerarySearchRequest;
import com.travelapi.multidestination.repository.ItineraryRepository;
import com.travelapi.multidestination.service.external.AmadeusFlightService;
//import com.travelapi.multidestination.service.external.CityDataService;
//import com.travelapi.multidestination.service.external.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsable de la génération d'itinéraires multi-destinations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ItineraryGeneratorService {

    private final ItineraryRepository itineraryRepository;
    private final AmadeusFlightService amadeusFlightService;
    //private final CityDataService cityDataService;
    //private final WeatherService weatherService;

    private static final int MAX_ITINERARIES_TO_GENERATE = 12;
    private static final double ACCOMMODATION_COST_PER_DAY = 50.0;
    private static final double FOOD_COST_PER_DAY = 20.0;

    /**
     * Génère des itinéraires basés sur les critères de recherche
     */
    @Async
    public List<Itinerary> generateItineraries(ItinerarySearch search) {
        log.info("Génération d'itinéraires pour la recherche {}", search.getId());

        try {
            // 1. Calculer la durée totale du voyage
            long totalDays = ChronoUnit.DAYS.between(search.getDepartureDate(), search.getReturnDate());
            log.info("Durée totale du voyage: {} jours", totalDays);

            if (totalDays < search.getNumberOfCities() * search.getMinDaysPerCity()) {
                log.warn("Durée totale insuffisante pour visiter le nombre de villes demandé");
                return Collections.emptyList();
            }

            // 2. Trouver des destinations potentielles en fonction des préférences
            List<String> potentialDestinations = findPotentialDestinations(search);
            log.info("Destinations potentielles: {}", potentialDestinations);

            if (potentialDestinations.size() < search.getNumberOfCities()) {
                log.warn("Pas assez de destinations potentielles trouvées");
                return Collections.emptyList();
            }

            // 3. Générer toutes les combinaisons possibles de villes
            List<List<String>> cityCombinations = generateCityCombinations(
                    potentialDestinations,
                    search.getNumberOfCities(),
                    search.getExcludedDestinations()
            );
            log.info("Nombre de combinaisons de villes générées: {}", cityCombinations.size());

            // 4. Pour chaque combinaison, générer les itinéraires possibles
            List<Itinerary> generatedItineraries = new ArrayList<>();

            for (List<String> combination : cityCombinations) {
                List<Itinerary> itinerariesForCombination = generateItinerariesForCityCombination(
                        combination,
                        search,
                        totalDays
                );

                generatedItineraries.addAll(itinerariesForCombination);

                if (generatedItineraries.size() >= MAX_ITINERARIES_TO_GENERATE) {
                    break;
                }
            }

            // 5. Trier et sauvegarder les itinéraires générés
            List<Itinerary> sortedItineraries = sortItineraries(generatedItineraries);
            itineraryRepository.saveAll(sortedItineraries);

            log.info("Génération d'itinéraires terminée. {} itinéraires générés", sortedItineraries.size());
            return sortedItineraries;

        } catch (Exception e) {
            log.error("Erreur lors de la génération d'itinéraires", e);
            return Collections.emptyList();
        }
    }

    /**
     * Trouve des destinations potentielles en fonction des préférences
     */
    private List<String> findPotentialDestinations(ItinerarySearch search) {
        // Dans une implémentation réelle, nous utiliserions une base de données ou un service externe
        // Pour cet exemple, nous retournons une liste prédéfinie de destinations

        Map<String, List<String>> destinationsByContinent = Map.of(
                "Europe", Arrays.asList("BCN", "MAD", "LIS", "POR", "PAR", "ROM", "AMS", "BER", "VAL", "SVQ", "MIL", "ATH"),
                "Asia", Arrays.asList("TYO", "BKK", "SIN", "HKG", "BJS", "SEL", "KUL", "SGN"),
                "America", Arrays.asList("NYC", "LAX", "MIA", "MEX", "RIO", "BOG", "LIM", "BUE")
        );

        Map<String, List<String>> destinationsByInterest = Map.of(
                "culture", Arrays.asList("BCN", "MAD", "PAR", "ROM", "ATH", "BER", "TYO", "NYC"),
                "beach", Arrays.asList("BCN", "LIS", "VAL", "BKK", "MIA", "RIO"),
                "food", Arrays.asList("BCN", "MAD", "PAR", "VAL", "ROM", "TYO", "BKK", "MEX"),
                "nature", Arrays.asList("POR", "SVQ", "BER", "BJS", "RIO", "LIM"),
                "shopping", Arrays.asList("PAR", "MIL", "BER", "HKG", "NYC", "LAX")
        );

        // Filtrer par continent
        Set<String> filteredDestinations = new HashSet<>();

        if (search.getContinentPreferences() != null && !search.getContinentPreferences().isEmpty()) {
            for (String continent : search.getContinentPreferences()) {
                List<String> continentDestinations = destinationsByContinent.getOrDefault(continent, Collections.emptyList());
                filteredDestinations.addAll(continentDestinations);
            }
        } else {
            // Si aucun continent n'est spécifié, inclure toutes les destinations
            destinationsByContinent.values().forEach(filteredDestinations::addAll);
        }

        // Filtrer par intérêts
        Set<String> interestDestinations = new HashSet<>();

        if (search.getInterestPreferences() != null && !search.getInterestPreferences().isEmpty()) {
            for (String interest : search.getInterestPreferences()) {
                List<String> interestDests = destinationsByInterest.getOrDefault(interest, Collections.emptyList());
                interestDestinations.addAll(interestDests);
            }

            // Intersection des destinations par continent et par intérêt
            filteredDestinations.retainAll(interestDestinations);
        }

        // Exclure la ville d'origine
        filteredDestinations.remove(search.getOrigin());

        // Exclure les destinations explicitement exclues
        if (search.getExcludedDestinations() != null) {
            filteredDestinations.removeAll(search.getExcludedDestinations());
        }

        return new ArrayList<>(filteredDestinations);
    }

    /**
     * Génère toutes les combinaisons possibles de villes
     */
    private List<List<String>> generateCityCombinations(List<String> destinations, int numberOfCities, List<String> excludedDestinations) {
        if (numberOfCities == 1) {
            return destinations.stream()
                    .filter(d -> excludedDestinations == null || !excludedDestinations.contains(d))
                    .map(Collections::singletonList)
                    .collect(Collectors.toList());
        }

        List<List<String>> result = new ArrayList<>();

        for (int i = 0; i < destinations.size(); i++) {
            String destination = destinations.get(i);

            if (excludedDestinations != null && excludedDestinations.contains(destination)) {
                continue;
            }

            List<String> remainingDestinations = new ArrayList<>(destinations.subList(i + 1, destinations.size()));
            List<List<String>> subCombinations = generateCityCombinations(remainingDestinations, numberOfCities - 1, excludedDestinations);

            for (List<String> subCombination : subCombinations) {
                List<String> combination = new ArrayList<>();
                combination.add(destination);
                combination.addAll(subCombination);
                result.add(combination);
            }
        }

        return result;
    }

    /**
     * Génère des itinéraires pour une combinaison de villes
     */
    private List<Itinerary> generateItinerariesForCityCombination(List<String> cityCodes, ItinerarySearch search, long totalDays) {
        List<Itinerary> itineraries = new ArrayList<>();

        // Générer différentes distributions de jours entre les villes
        List<List<Integer>> dayDistributions = generateDayDistributions(
                (int) totalDays,
                cityCodes.size(),
                search.getMinDaysPerCity(),
                search.getMaxDaysPerCity()
        );

        for (List<Integer> dayDistribution : dayDistributions) {
            Itinerary itinerary = createItinerary(cityCodes, dayDistribution, search);

            if (itinerary != null && itinerary.getTotalPrice() <= search.getBudget()) {
                itineraries.add(itinerary);
            }

            if (itineraries.size() >= 2) {  // Limiter le nombre d'itinéraires par combinaison
                break;
            }
        }

        return itineraries;
    }

    /**
     * Génère les distributions possibles de jours entre les villes
     */
    private List<List<Integer>> generateDayDistributions(int totalDays, int numberOfCities, int minDays, int maxDays) {
        List<List<Integer>> distributions = new ArrayList<>();

        // Générer une distribution de base où chaque ville a le minimum de jours
        int remainingDays = totalDays - (numberOfCities * minDays);

        if (remainingDays < 0) {
            return distributions;
        }

        // Ajouter quelques distributions types pour simplifier
        List<Integer> baseDistribution = new ArrayList<>(Collections.nCopies(numberOfCities, minDays));

        // Distribution 1: Répartir les jours restants uniformément
        List<Integer> distribution1 = new ArrayList<>(baseDistribution);
        int daysPerCity = remainingDays / numberOfCities;
        int extraDays = remainingDays % numberOfCities;

        for (int i = 0; i < numberOfCities; i++) {
            distribution1.set(i, distribution1.get(i) + daysPerCity + (i < extraDays ? 1 : 0));

            // Vérifier que le nombre de jours ne dépasse pas le maximum
            if (distribution1.get(i) > maxDays) {
                distribution1.set(i, maxDays);
            }
        }

        distributions.add(distribution1);

        // Distribution 2: Plus de jours dans la première ville
        if (numberOfCities > 1 && remainingDays > 0) {
            List<Integer> distribution2 = new ArrayList<>(baseDistribution);
            int extraDaysForFirst = Math.min(remainingDays, maxDays - minDays);
            distribution2.set(0, distribution2.get(0) + extraDaysForFirst);

            remainingDays -= extraDaysForFirst;
            int daysPerRemaining = numberOfCities > 1 ? remainingDays / (numberOfCities - 1) : 0;

            for (int i = 1; i < numberOfCities; i++) {
                distribution2.set(i, distribution2.get(i) + daysPerRemaining);

                if (distribution2.get(i) > maxDays) {
                    distribution2.set(i, maxDays);
                }
            }

            distributions.add(distribution2);
        }

        // Distribution 3: Plus de jours dans la dernière ville
        if (numberOfCities > 1 && remainingDays > 0) {
            List<Integer> distribution3 = new ArrayList<>(baseDistribution);
            int extraDaysForLast = Math.min(remainingDays, maxDays - minDays);
            distribution3.set(numberOfCities - 1, distribution3.get(numberOfCities - 1) + extraDaysForLast);

            remainingDays -= extraDaysForLast;
            int daysPerRemaining = numberOfCities > 1 ? remainingDays / (numberOfCities - 1) : 0;

            for (int i = 0; i < numberOfCities - 1; i++) {
                distribution3.set(i, distribution3.get(i) + daysPerRemaining);

                if (distribution3.get(i) > maxDays) {
                    distribution3.set(i, maxDays);
                }
            }

            distributions.add(distribution3);
        }

        return distributions;
    }

    /**
     * Crée un itinéraire complet avec vols, villes et détails de budget
     */
    private Itinerary createItinerary(List<String> cityCodes, List<Integer> dayDistribution, ItinerarySearch search) {
        try {
            // Créer les segments de vol
            List<String> origins = new ArrayList<>();
            List<String> destinations = new ArrayList<>();
            List<LocalDate> departureDates = new ArrayList<>();

            // Vol initial depuis la ville d'origine
            origins.add(search.getOrigin());
            destinations.add(cityCodes.get(0));
            departureDates.add(search.getDepartureDate());

            // Vols entre les villes de l'itinéraire
            LocalDate currentDate = search.getDepartureDate();

            for (int i = 0; i < cityCodes.size() - 1; i++) {
                currentDate = currentDate.plusDays(dayDistribution.get(i));

                origins.add(cityCodes.get(i));
                destinations.add(cityCodes.get(i + 1));
                departureDates.add(currentDate);
            }

            // Vol de retour vers la ville d'origine
            currentDate = currentDate.plusDays(dayDistribution.get(cityCodes.size() - 1));

            origins.add(cityCodes.get(cityCodes.size() - 1));
            destinations.add(search.getOrigin());
            departureDates.add(currentDate);

            // Rechercher les vols avec l'API Amadeus
            List<FlightLeg> legs = amadeusFlightService.searchMultiCityFlights(
                    origins,
                    destinations,
                    departureDates,
                    search.getBudget()
            );

            if (legs.size() != origins.size()) {
                log.warn("Impossible de trouver tous les vols nécessaires");
                return null;
            }

            // Créer les objets City
            List<City> cities = new ArrayList<>();
            currentDate = search.getDepartureDate();

            for (int i = 0; i < cityCodes.size(); i++) {
                String cityCode = cityCodes.get(i);
                int stayDuration = dayDistribution.get(i);

                LocalDate arrivalDate = currentDate;
                LocalDate departureDate = currentDate.plusDays(stayDuration);
                currentDate = departureDate;

                City city = createCityObject(cityCode, stayDuration + " days", arrivalDate, departureDate);
                cities.add(city);
            }

            // Calculer le coût total des vols
            double totalFlightCost = legs.stream().mapToDouble(FlightLeg::getPrice).sum();

            // Calculer les coûts estimés d'hébergement et de nourriture
            int totalStayDays = dayDistribution.stream().mapToInt(Integer::intValue).sum();
            double accommodationCost = totalStayDays * ACCOMMODATION_COST_PER_DAY;
            double foodCost = totalStayDays * FOOD_COST_PER_DAY;

            // Créer la répartition du budget
            BudgetBreakdown budgetBreakdown = BudgetBreakdown.builder()
                    .flights(totalFlightCost)
                    .estimatedAccommodation(accommodationCost)
                    .estimatedFood(foodCost)
                    .total(totalFlightCost + accommodationCost + foodCost)
                    .remainingBudget(search.getBudget() - (totalFlightCost + accommodationCost + foodCost))
                    .build();

            // Générer des conseils d'optimisation
            List<String> optimizationTips = generateOptimizationTips(legs, cities, budgetBreakdown);

            // Créer et retourner l'itinéraire complet
            return Itinerary.builder()
                    .id("itin-" + UUID.randomUUID().toString().substring(0, 6))
                    .searchId(search.getId())
                    .totalPrice(budgetBreakdown.getTotal())
                    .totalDuration(totalStayDays + " days")
                    .legs(legs)
                    .cities(cities)
                    .budgetBreakdown(budgetBreakdown)
                    .optimizationTips(optimizationTips)
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors de la création de l'itinéraire", e);
            return null;
        }
    }

    /**
     * Crée un objet City avec ses détails
     */
    private City createCityObject(String cityCode, String stayDuration, LocalDate arrivalDate, LocalDate departureDate) {
        // Dans une implémentation réelle, nous utiliserions un service pour obtenir les détails de la ville
        Map<String, String> cityNames = Map.of(
                "BCN", "Barcelona",
                "MAD", "Madrid",
                "LIS", "Lisbon",
                "VAL", "Valencia",
                "POR", "Porto",
                "PAR", "Paris",
                "ROM", "Rome",
                "SVQ", "Seville"
        );

        Map<String, String> countries = Map.of(
                "BCN", "Spain",
                "MAD", "Spain",
                "LIS", "Portugal",
                "VAL", "Spain",
                "POR", "Portugal",
                "PAR", "France",
                "ROM", "Italy",
                "SVQ", "Spain"
        );

        Map<String, List<String>> highlights = Map.of(
                "BCN", Arrays.asList("Sagrada Familia", "Park Güell", "La Rambla", "Barceloneta Beach"),
                "MAD", Arrays.asList("Prado Museum", "Royal Palace", "Retiro Park", "Plaza Mayor"),
                "LIS", Arrays.asList("Belém Tower", "Jerónimos Monastery", "Alfama District", "Time Out Market"),
                "VAL", Arrays.asList("City of Arts and Sciences", "Valencia Cathedral", "Malvarrosa Beach", "Central Market"),
                "POR", Arrays.asList("Ribeira District", "Dom Luís I Bridge", "Porto Cathedral", "Wine Cellars"),
                "SVQ", Arrays.asList("Alcázar Palace", "Seville Cathedral", "Plaza de España", "Barrio Santa Cruz")
        );

        // Simuler des prévisions météo
        Random random = new Random();
        int avgTemp = 20 + random.nextInt(16); // Entre 20 et 35 degrés
        String condition = random.nextBoolean() ? "Sunny" : "Partly Cloudy";

        WeatherForecast weather = WeatherForecast.builder()
                .averageTemp(avgTemp)
                .condition(condition)
                .build();

        return City.builder()
                .code(cityCode)
                .name(cityNames.getOrDefault(cityCode, "Unknown City"))
                .country(countries.getOrDefault(cityCode, "Unknown Country"))
                .stayDuration(stayDuration)
                .arrivalDate(arrivalDate)
                .departureDate(departureDate)
                .highlights(highlights.getOrDefault(cityCode, Collections.emptyList()))
                .weatherForecast(weather)
                .build();
    }

    /**
     * Génère des conseils d'optimisation pour l'itinéraire
     */
    private List<String> generateOptimizationTips(List<FlightLeg> legs, List<City> cities, BudgetBreakdown budget) {
        List<String> tips = new ArrayList<>();

        // Simuler quelques conseils d'optimisation
        if (legs.size() > 2) {
            tips.add("Booking accommodation in " + cities.get(0).getName() + " 1 week earlier could save ~€30");
        }

        if (budget.getTotal() > 700) {
            tips.add("Flying on Tuesday instead of Thursday could reduce the return flight cost by ~€20");
        }

        if (budget.getEstimatedAccommodation() > 250) {
            tips.add("Staying in hostels instead of hotels could save ~€50");
        }

        if (legs.size() >= 3) {
            tips.add("Booking all flights together as a multi-city package could reduce total flight costs by ~€25");
        }

        Collections.shuffle(tips);
        return tips.subList(0, Math.min(2, tips.size()));
    }

    /**
     * Trie les itinéraires générés selon différents critères
     */
    private List<Itinerary> sortItineraries(List<Itinerary> itineraries) {
        // Trier par prix total croissant
        return itineraries.stream()
                .sorted(Comparator.comparingDouble(Itinerary::getTotalPrice))
                .limit(MAX_ITINERARIES_TO_GENERATE)
                .collect(Collectors.toList());
    }
}