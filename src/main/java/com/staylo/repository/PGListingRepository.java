package com.staylo.repository;

import com.staylo.entity.PGListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PGListingRepository extends JpaRepository<PGListing, Long> {
    List<PGListing> findByOwnerId(Long ownerId);
    List<PGListing> findByIsAvailableTrue();
    List<PGListing> findByCityIgnoreCase(String city);
    List<PGListing> findByType(PGListing.ListingType type);

    @Query("SELECT p FROM PGListing p WHERE p.isAvailable = true AND p.city = :city AND p.monthlyRent <= :maxRent")
    List<PGListing> findAvailableByFilters(String city, Double maxRent);
}
