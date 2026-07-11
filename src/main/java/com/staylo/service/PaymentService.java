package com.staylo.service;

import com.staylo.dto.PaymentDTO;
import com.staylo.entity.Payment;
import com.staylo.entity.Student;
import com.staylo.exception.ResourceNotFoundException;
import com.staylo.repository.PaymentRepository;
import com.staylo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public PaymentDTO.Response recordPayment(PaymentDTO.Request request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Payment payment = Payment.builder()
                .student(student)
                .amount(request.getAmount())
                .type(request.getType())
                .status(request.getStatus() != null ? request.getStatus() : Payment.PaymentStatus.PAID)
                .paymentDate(request.getPaymentDate())
                .dueDate(request.getDueDate())
                .transactionId(request.getTransactionId())
                .paymentMode(request.getPaymentMode())
                .remarks(request.getRemarks())
                .build();

        return toResponse(paymentRepository.save(payment));
    }

    public List<PaymentDTO.Response> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PaymentDTO.Response> getPaymentsByStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student", "id", studentId);
        }
        return paymentRepository.findByStudentId(studentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<PaymentDTO.Response> getOverduePayments() {
        return paymentRepository.findAllOverduePayments()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PaymentDTO.Response getPaymentById(Long id) {
        return toResponse(paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id)));
    }

    @Transactional
    public PaymentDTO.Response updatePaymentStatus(Long id, Payment.PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
        payment.setStatus(status);
        return toResponse(paymentRepository.save(payment));
    }

    public Double getTotalPaidByStudent(Long studentId) {
        Double total = paymentRepository.totalPaidByStudent(studentId);
        return total != null ? total : 0.0;
    }

    // ---- Mapper ----
    private PaymentDTO.Response toResponse(Payment payment) {
        return PaymentDTO.Response.builder()
                .id(payment.getId())
                .studentId(payment.getStudent().getId())
                .studentName(payment.getStudent().getUser().getName())
                .enrollmentNo(payment.getStudent().getEnrollmentNo())
                .amount(payment.getAmount())
                .type(payment.getType())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .dueDate(payment.getDueDate())
                .transactionId(payment.getTransactionId())
                .paymentMode(payment.getPaymentMode())
                .remarks(payment.getRemarks())
                .build();
    }
}
