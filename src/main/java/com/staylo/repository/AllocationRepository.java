package com.staylo.repository;

import com.staylo.entity.Allocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {
    List<Allocation> findByStudentId(Long studentId);
    List<Allocation> findByRoomId(Long roomId);
    List<Allocation> findByStatus(Allocation.AllocationStatus status);

    Optional<Allocation> findByStudentIdAndStatus(Long studentId, Allocation.AllocationStatus status);

    @Query("SELECT COUNT(a) FROM Allocation a WHERE a.room.id = :roomId AND a.status = 'ACTIVE'")
    long countActiveAllocationsForRoom(Long roomId);

    boolean existsByStudentIdAndStatus(Long studentId, Allocation.AllocationStatus status);
}
