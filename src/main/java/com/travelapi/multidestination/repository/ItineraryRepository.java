package com.travelapi.multidestination.repository;

import com.travelapi.multidestination.model.Itinerary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryRepository extends MongoRepository<Itinerary, String> {
    
    List<Itinerary> findBySearchId(String searchId);
    
    List<Itinerary> findByTotalPriceLessThanEqual(double maxPrice);
    
    List<Itinerary> findTop5ByOrderByTotalPriceAsc();
}
