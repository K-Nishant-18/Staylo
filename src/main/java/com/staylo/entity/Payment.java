package com.staylo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_mode")
    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    @Column
    private String remarks;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum PaymentType {
        HOSTEL_FEE, PG_RENT, MESS_FEE, MAINTENANCE, OTHER
    }

    public enum PaymentStatus {
        PAID, PENDING, OVERDUE, CANCELLED
    }

    public enum PaymentMode {
        CASH, ONLINE, UPI, BANK_TRANSFER
    }
}
