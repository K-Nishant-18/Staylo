package com.staylo.repository;

import com.staylo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudentId(Long studentId);
    List<Payment> findByStatus(Payment.PaymentStatus status);
    List<Payment> findByStudentIdAndStatus(Long studentId, Payment.PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.status = 'OVERDUE' ORDER BY p.dueDate ASC")
    List<Payment> findAllOverduePayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student.id = :studentId AND p.status = 'PAID'")
    Double totalPaidByStudent(Long studentId);
}
