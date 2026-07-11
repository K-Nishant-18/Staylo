package com.staylo.controller;

import com.staylo.dto.ApiResponse;
import com.staylo.dto.PaymentDTO;
import com.staylo.entity.Payment;
import com.staylo.service.PaymentService;
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
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payments", description = "Payment recording and tracking endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Record a payment")
    public ResponseEntity<ApiResponse<PaymentDTO.Response>> record(
            @Valid @RequestBody PaymentDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment recorded successfully",
                        paymentService.recordPayment(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get all payments")
    public ResponseEntity<ApiResponse<List<PaymentDTO.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getAllPayments()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<PaymentDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentById(id)));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")
    @Operation(summary = "Get all payments for a student")
    public ResponseEntity<ApiResponse<List<PaymentDTO.Response>>> getByStudent(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getPaymentsByStudent(studentId)));
    }

    @GetMapping("/student/{studentId}/total-paid")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")
    @Operation(summary = "Get total amount paid by a student")
    public ResponseEntity<ApiResponse<Double>> getTotalPaid(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getTotalPaidByStudent(studentId)));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get all overdue payments")
    public ResponseEntity<ApiResponse<List<PaymentDTO.Response>>> getOverdue() {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getOverduePayments()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Update payment status")
    public ResponseEntity<ApiResponse<PaymentDTO.Response>> updateStatus(
            @PathVariable Long id,
            @RequestParam Payment.PaymentStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Payment status updated",
                paymentService.updatePaymentStatus(id, status)));
    }
}
