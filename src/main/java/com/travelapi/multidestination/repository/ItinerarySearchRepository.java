package com.travelapi.multidestination.repository;

import com.travelapi.multidestination.model.ItinerarySearch;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItinerarySearchRepository extends MongoRepository<ItinerarySearch, String> {
    
    List<ItinerarySearch> findByOrigin(String origin);
    
    List<ItinerarySearch> findByDepartureDateBetween(LocalDate start, LocalDate end);
    
    List<ItinerarySearch> findByBudgetLessThanEqual(double budget);
}
