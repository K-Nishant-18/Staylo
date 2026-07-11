package com.staylo.service;

import com.staylo.dto.PGListingDTO;
import com.staylo.entity.PGListing;
import com.staylo.entity.User;
import com.staylo.exception.ResourceNotFoundException;
import com.staylo.exception.StayloException;
import com.staylo.repository.PGListingRepository;
import com.staylo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PGListingService {

    private final PGListingRepository pgListingRepository;
    private final UserRepository userRepository;

    @Transactional
    public PGListingDTO.Response createListing(PGListingDTO.Request request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        PGListing listing = PGListing.builder()
                .owner(owner)
                .title(request.getTitle())
                .address(request.getAddress())
                .city(request.getCity())
                .monthlyRent(request.getMonthlyRent())
                .type(request.getType())
                .totalRooms(request.getTotalRooms())
                .availableRooms(request.getAvailableRooms())
                .amenities(request.getAmenities())
                .contactNo(request.getContactNo())
                .genderPreference(request.getGenderPreference() != null
                        ? request.getGenderPreference() : PGListing.GenderPreference.ANY)
                .build();

        return toResponse(pgListingRepository.save(listing));
    }

    public List<PGListingDTO.Response> getAllListings() {
        return pgListingRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PGListingDTO.Response> getAvailableListings() {
        return pgListingRepository.findByIsAvailableTrue().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PGListingDTO.Response> getListingsByCity(String city) {
        return pgListingRepository.findByCityIgnoreCase(city).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PGListingDTO.Response> filterListings(String city, Double maxRent) {
        return pgListingRepository.findAvailableByFilters(city, maxRent)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PGListingDTO.Response getListingById(Long id) {
        return toResponse(pgListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", id)));
    }

    public List<PGListingDTO.Response> getMyListings() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return pgListingRepository.findByOwnerId(owner.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public PGListingDTO.Response updateListing(Long id, PGListingDTO.Request request) {
        PGListing listing = pgListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", id));

        // Only the owner or admin can update
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!listing.getOwner().getEmail().equals(email)) {
            User user = userRepository.findByEmail(email).orElseThrow();
            if (user.getRole() != User.Role.ADMIN) {
                throw new StayloException("You can only update your own listings");
            }
        }

        listing.setTitle(request.getTitle());
        listing.setAddress(request.getAddress());
        listing.setCity(request.getCity());
        listing.setMonthlyRent(request.getMonthlyRent());
        listing.setType(request.getType());
        listing.setTotalRooms(request.getTotalRooms());
        listing.setAvailableRooms(request.getAvailableRooms());
        listing.setAmenities(request.getAmenities());
        listing.setContactNo(request.getContactNo());

        return toResponse(pgListingRepository.save(listing));
    }

    @Transactional
    public void deleteListing(Long id) {
        PGListing listing = pgListingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing", "id", id));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!listing.getOwner().getEmail().equals(email)) {
            User user = userRepository.findByEmail(email).orElseThrow();
            if (user.getRole() != User.Role.ADMIN) {
                throw new StayloException("You can only delete your own listings");
            }
        }

        pgListingRepository.deleteById(id);
    }

    // ---- Mapper ----
    private PGListingDTO.Response toResponse(PGListing listing) {
        return PGListingDTO.Response.builder()
                .id(listing.getId())
                .ownerName(listing.getOwner().getName())
                .ownerEmail(listing.getOwner().getEmail())
                .title(listing.getTitle())
                .address(listing.getAddress())
                .city(listing.getCity())
                .monthlyRent(listing.getMonthlyRent())
                .type(listing.getType())
                .totalRooms(listing.getTotalRooms())
                .availableRooms(listing.getAvailableRooms())
                .amenities(listing.getAmenities())
                .contactNo(listing.getContactNo())
                .isAvailable(listing.isAvailable())
                .genderPreference(listing.getGenderPreference())
                .build();
    }
}
