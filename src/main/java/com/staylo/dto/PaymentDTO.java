package com.staylo.dto;

import com.staylo.entity.Payment;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

public class PaymentDTO {

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        @NotNull(message = "Student ID is required")
        private Long studentId;

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be positive")
        private Double amount;

        @NotNull(message = "Payment type is required")
        private Payment.PaymentType type;

        private Payment.PaymentStatus status;
        private LocalDate paymentDate;
        private LocalDate dueDate;
        private String transactionId;
        private Payment.PaymentMode paymentMode;
        private String remarks;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long studentId;
        private String studentName;
        private String enrollmentNo;
        private Double amount;
        private Payment.PaymentType type;
        private Payment.PaymentStatus status;
        private LocalDate paymentDate;
        private LocalDate dueDate;
        private String transactionId;
        private Payment.PaymentMode paymentMode;
        private String remarks;
    }
}
