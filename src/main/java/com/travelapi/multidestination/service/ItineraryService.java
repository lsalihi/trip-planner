package com.travelapi.multidestination.service;

import com.travelapi.multidestination.model.Itinerary;
import com.travelapi.multidestination.model.ItinerarySearch;
import com.travelapi.multidestination.model.dto.ItinerarySearchRequest;
import com.travelapi.multidestination.repository.ItineraryRepository;
import com.travelapi.multidestination.repository.ItinerarySearchRepository;
import com.travelapi.multidestination.service.external.AmadeusFlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final ItinerarySearchRepository itinerarySearchRepository;
    private final ItineraryGeneratorService itineraryGeneratorService;
    
    /**
     * Crée une nouvelle recherche d'itinéraire et lance la recherche d'itinéraires correspondants
     */
    public ItinerarySearch createSearch(ItinerarySearchRequest request) {
        log.info("Création d'une nouvelle recherche d'itinéraire depuis {}", request.getOrigin());
        
        // Convertir la requête en entité ItinerarySearch
        ItinerarySearch search = ItinerarySearch.builder()
                .origin(request.getOrigin())
                .departureDate(request.getDepartureDate())
                .returnDate(request.getReturnDate())
                .budget(request.getBudget())
                .numberOfCities(request.getNumberOfCities())
                .minDaysPerCity(request.getMinDaysPerCity())
                .maxDaysPerCity(request.getMaxDaysPerCity())
                .continentPreferences(request.getPreferences() != null ? request.getPreferences().getContinent() : null)
                .interestPreferences(request.getPreferences() != null ? request.getPreferences().getInterests() : null)
                .excludedDestinations(request.getPreferences() != null ? request.getPreferences().getExcludedDestinations() : null)
                .createdAt(LocalDateTime.now().toLocalDate())
                .build();
        
        // Sauvegarder la recherche
        ItinerarySearch savedSearch = itinerarySearchRepository.save(search);
        
        // TODO: Implémenter la logique pour générer des itinéraires basés sur cette recherche
        // Cette partie sera implémentée dans l'étape 007 (implementer_services_integration_apis_externes)
        itineraryGeneratorService.generateItineraries(savedSearch);

        return savedSearch;
    }
    
    /**
     * Récupère tous les itinéraires correspondant à une recherche
     */
    public List<Itinerary> getItinerariesForSearch(String searchId) {
        log.info("Récupération des itinéraires pour la recherche {}", searchId);
        return itineraryRepository.findBySearchId(searchId);
    }
    
    /**
     * Récupère un itinéraire par son ID
     */
    public Optional<Itinerary> getItineraryById(String id) {
        log.info("Récupération de l'itinéraire {}", id);
        return itineraryRepository.findById(id);
    }
    
    /**
     * Récupère les itinéraires les moins chers
     */
    public List<Itinerary> getCheapestItineraries() {
        log.info("Récupération des 5 itinéraires les moins chers");
        return itineraryRepository.findTop5ByOrderByTotalPriceAsc();
    }
    
    /**
     * Récupère les itinéraires dans une fourchette de prix
     */
    public List<Itinerary> getItinerariesWithinBudget(double maxBudget) {
        log.info("Récupération des itinéraires avec un budget maximum de {}", maxBudget);
        return itineraryRepository.findByTotalPriceLessThanEqual(maxBudget);
    }
}
