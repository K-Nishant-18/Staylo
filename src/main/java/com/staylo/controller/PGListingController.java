package com.staylo.controller;

import com.staylo.dto.ApiResponse;
import com.staylo.dto.PGListingDTO;
import com.staylo.service.PGListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
@Tag(name = "PG & Home Listings", description = "Browse and manage nearby accommodation listings")
public class PGListingController {

    private final PGListingService pgListingService;

    // Public — no auth needed
    @GetMapping
    @Operation(summary = "Browse all available listings (public)")
    public ResponseEntity<ApiResponse<List<PGListingDTO.Response>>> getAvailable(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double maxRent) {
        List<PGListingDTO.Response> listings;
        if (city != null && maxRent != null) {
            listings = pgListingService.filterListings(city, maxRent);
        } else if (city != null) {
            listings = pgListingService.getListingsByCity(city);
        } else {
            listings = pgListingService.getAvailableListings();
        }
        return ResponseEntity.ok(ApiResponse.success(listings));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get listing details by ID (public)")
    public ResponseEntity<ApiResponse<PGListingDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(pgListingService.getListingById(id)));
    }

    // Protected
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROPERTY_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new listing")
    public ResponseEntity<ApiResponse<PGListingDTO.Response>> create(
            @Valid @RequestBody PGListingDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Listing created successfully",
                        pgListingService.createListing(request)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROPERTY_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get my listings (for logged-in property owner)")
    public ResponseEntity<ApiResponse<List<PGListingDTO.Response>>> getMyListings() {
        return ResponseEntity.ok(ApiResponse.success(pgListingService.getMyListings()));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get all listings including unavailable (Admin only)")
    public ResponseEntity<ApiResponse<List<PGListingDTO.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(pgListingService.getAllListings()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROPERTY_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a listing")
    public ResponseEntity<ApiResponse<PGListingDTO.Response>> update(
            @PathVariable Long id,
            @Valid @RequestBody PGListingDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Listing updated successfully",
                pgListingService.updateListing(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROPERTY_OWNER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a listing")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        pgListingService.deleteListing(id);
        return ResponseEntity.ok(ApiResponse.success("Listing deleted successfully", null));
    }
}
