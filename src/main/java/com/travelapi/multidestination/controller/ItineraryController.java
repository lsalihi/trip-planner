package com.travelapi.multidestination.controller;

import com.travelapi.multidestination.model.Itinerary;
import com.travelapi.multidestination.model.ItinerarySearch;
import com.travelapi.multidestination.model.dto.ItinerarySearchRequest;
import com.travelapi.multidestination.service.ItineraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/itineraries")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Itinerary API", description = "API pour la recherche et la gestion des itinéraires multi-destinations")
public class ItineraryController {

    private final ItineraryService itineraryService;

    @PostMapping("/search")
    @Operation(
        summary = "Rechercher des itinéraires",
        description = "Crée une nouvelle recherche d'itinéraires multi-destinations basée sur les critères spécifiés",
        responses = {
            @ApiResponse(responseCode = "201", description = "Recherche créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide")
        }
    )
    public ResponseEntity<Map<String, Object>> searchItineraries(
            @Valid @RequestBody ItinerarySearchRequest request) {
        log.info("Nouvelle requête de recherche d'itinéraires depuis {}", request.getOrigin());
        
        ItinerarySearch search = itineraryService.createSearch(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("searchId", search.getId());
        response.put("message", "Recherche d'itinéraires initiée avec succès");
        
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/search/{searchId}")
    @Operation(
        summary = "Obtenir les résultats d'une recherche",
        description = "Récupère tous les itinéraires correspondant à une recherche spécifique",
        responses = {
            @ApiResponse(responseCode = "200", description = "Itinéraires récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Recherche non trouvée")
        }
    )
    public ResponseEntity<List<Itinerary>> getItinerariesForSearch(
            @Parameter(description = "ID de la recherche") @PathVariable String searchId) {
        log.info("Récupération des itinéraires pour la recherche {}", searchId);
        
        List<Itinerary> itineraries = itineraryService.getItinerariesForSearch(searchId);
        
        if (itineraries.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(itineraries, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtenir un itinéraire par ID",
        description = "Récupère les détails d'un itinéraire spécifique",
        responses = {
            @ApiResponse(
                responseCode = "200", 
                description = "Itinéraire trouvé",
                content = @Content(schema = @Schema(implementation = Itinerary.class))
            ),
            @ApiResponse(responseCode = "404", description = "Itinéraire non trouvé")
        }
    )
    public ResponseEntity<Itinerary> getItineraryById(
            @Parameter(description = "ID de l'itinéraire") @PathVariable String id) {
        log.info("Récupération de l'itinéraire {}", id);
        
        return itineraryService.getItineraryById(id)
                .map(itinerary -> new ResponseEntity<>(itinerary, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/cheapest")
    @Operation(
        summary = "Obtenir les itinéraires les moins chers",
        description = "Récupère les 5 itinéraires les moins chers disponibles"
    )
    public ResponseEntity<List<Itinerary>> getCheapestItineraries() {
        log.info("Récupération des itinéraires les moins chers");
        
        List<Itinerary> itineraries = itineraryService.getCheapestItineraries();
        
        return new ResponseEntity<>(itineraries, HttpStatus.OK);
    }

    @GetMapping("/budget/{maxBudget}")
    @Operation(
        summary = "Obtenir les itinéraires dans un budget",
        description = "Récupère tous les itinéraires dont le prix total est inférieur ou égal au budget spécifié"
    )
    public ResponseEntity<List<Itinerary>> getItinerariesWithinBudget(
            @Parameter(description = "Budget maximum") @PathVariable double maxBudget) {
        log.info("Récupération des itinéraires avec un budget maximum de {}", maxBudget);
        
        List<Itinerary> itineraries = itineraryService.getItinerariesWithinBudget(maxBudget);
        
        return new ResponseEntity<>(itineraries, HttpStatus.OK);
    }
}
