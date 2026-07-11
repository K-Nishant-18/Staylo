package com.staylo.dto;

import com.staylo.entity.PGListing;
import jakarta.validation.constraints.*;
import lombok.*;

public class PGListingDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank(message = "Title is required")
        private String title;

        @NotBlank(message = "Address is required")
        private String address;

        @NotBlank(message = "City is required")
        private String city;

        @NotNull(message = "Monthly rent is required")
        @Positive(message = "Rent must be positive")
        private Double monthlyRent;

        @NotNull(message = "Listing type is required")
        private PGListing.ListingType type;

        private Integer totalRooms;
        private Integer availableRooms;
        private String amenities;
        private String contactNo;
        private PGListing.GenderPreference genderPreference;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String ownerName;
        private String ownerEmail;
        private String title;
        private String address;
        private String city;
        private Double monthlyRent;
        private PGListing.ListingType type;
        private Integer totalRooms;
        private Integer availableRooms;
        private String amenities;
        private String contactNo;
        private boolean isAvailable;
        private PGListing.GenderPreference genderPreference;
    }
}
