package com.staylo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pg_listings")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PGListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(name = "monthly_rent", nullable = false)
    private Double monthlyRent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType type;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "available_rooms")
    private Integer availableRooms;

    @Column(length = 1000)
    private String amenities; // Comma-separated: WiFi, Meals, Laundry, etc.

    @Column(name = "contact_no")
    private String contactNo;

    @Column(name = "is_available")
    @Builder.Default
    private boolean isAvailable = true;

    @Column(name = "gender_preference")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GenderPreference genderPreference = GenderPreference.ANY;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ListingType {
        PG, FLAT, ROOM, HOSTEL
    }

    public enum GenderPreference {
        MALE, FEMALE, ANY
    }
}
