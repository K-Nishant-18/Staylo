package com.staylo.dto;

import com.staylo.entity.HostelRoom;
import jakarta.validation.constraints.*;
import lombok.*;

public class RoomDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotBlank(message = "Room number is required")
        private String roomNumber;

        @NotBlank(message = "Hostel block is required")
        private String hostelBlock;

        @NotNull(message = "Floor is required")
        private Integer floor;

        @NotNull(message = "Room type is required")
        private HostelRoom.RoomType type;

        @NotNull(message = "Capacity is required")
        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 6, message = "Capacity must be at most 6")
        private Integer capacity;

        @NotNull(message = "Monthly fee is required")
        @Positive(message = "Monthly fee must be positive")
        private Double monthlyFee;

        private boolean hasAttachedBathroom;
        private boolean hasAc;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String roomNumber;
        private String hostelBlock;
        private Integer floor;
        private HostelRoom.RoomType type;
        private Integer capacity;
        private Integer occupied;
        private Double monthlyFee;
        private boolean hasAttachedBathroom;
        private boolean hasAc;
        private HostelRoom.RoomStatus status;
        private boolean available;
    }
}
