package com.staylo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hostel_rooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HostelRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Column(name = "hostel_block", nullable = false)
    private String hostelBlock;

    @Column(nullable = false)
    private Integer floor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    @Builder.Default
    private Integer occupied = 0;

    @Column(name = "monthly_fee", nullable = false)
    private Double monthlyFee;

    @Column(name = "has_attached_bathroom")
    @Builder.Default
    private boolean hasAttachedBathroom = false;

    @Column(name = "has_ac")
    @Builder.Default
    private boolean hasAc = false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RoomStatus status = RoomStatus.AVAILABLE;

    public boolean isAvailable() {
        return occupied < capacity && status == RoomStatus.AVAILABLE;
    }

    public enum RoomType {
        SINGLE, DOUBLE, TRIPLE
    }

    public enum RoomStatus {
        AVAILABLE, FULL, MAINTENANCE
    }
}
