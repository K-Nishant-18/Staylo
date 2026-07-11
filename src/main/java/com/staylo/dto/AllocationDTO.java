package com.staylo.dto;

import com.staylo.entity.Allocation;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

public class AllocationDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Room ID is required")
        private Long roomId;

        @NotNull(message = "Check-in date is required")
        private LocalDate checkInDate;

        private LocalDate checkOutDate;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long studentId;
        private String studentName;
        private String enrollmentNo;
        private Long roomId;
        private String roomNumber;
        private String hostelBlock;
        private LocalDate checkInDate;
        private LocalDate checkOutDate;
        private Allocation.AllocationStatus status;
        private String allocatedBy;
    }
}
