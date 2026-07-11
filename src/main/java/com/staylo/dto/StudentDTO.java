package com.staylo.dto;

import jakarta.validation.constraints.*;
import lombok.*;

public class StudentDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotBlank(message = "Enrollment number is required")
        private String enrollmentNo;

        @NotBlank(message = "Course is required")
        private String course;

        @NotNull(message = "Year is required")
        @Min(value = 1, message = "Year must be at least 1")
        @Max(value = 5, message = "Year must be at most 5")
        private Integer year;

        @NotBlank(message = "Contact number is required")
        @Pattern(regexp = "^[0-9]{10}$", message = "Contact number must be 10 digits")
        private String contactNo;

        private String guardianName;
        private String guardianContact;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long userId;
        private String name;
        private String email;
        private String enrollmentNo;
        private String course;
        private Integer year;
        private String contactNo;
        private String guardianName;
        private String guardianContact;
    }
}
