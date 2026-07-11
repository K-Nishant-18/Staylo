package com.staylo.service;

import com.staylo.dto.AllocationDTO;
import com.staylo.entity.Allocation;
import com.staylo.entity.HostelRoom;
import com.staylo.entity.Student;
import com.staylo.exception.ResourceNotFoundException;
import com.staylo.exception.StayloException;
import com.staylo.repository.AllocationRepository;
import com.staylo.repository.HostelRoomRepository;
import com.staylo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final StudentRepository studentRepository;
    private final HostelRoomRepository roomRepository;

    @Transactional
    public AllocationDTO.Response allocateRoom(AllocationDTO.Request request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        // Check student doesn't already have an active allocation
        if (allocationRepository.existsByStudentIdAndStatus(request.getStudentId(), Allocation.AllocationStatus.ACTIVE)) {
            throw new StayloException("Student already has an active room allocation. Vacate first.");
        }

        HostelRoom room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", request.getRoomId()));

        // Check room availability
        if (!room.isAvailable()) {
            throw new StayloException("Room " + room.getRoomNumber() + " is not available");
        }

        // Get currently logged-in user's email
        String allocatedBy = SecurityContextHolder.getContext().getAuthentication().getName();

        Allocation allocation = Allocation.builder()
                .student(student)
                .room(room)
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .allocatedBy(allocatedBy)
                .build();

        // Increment room occupancy
        room.setOccupied(room.getOccupied() + 1);
        if (room.getOccupied() >= room.getCapacity()) {
            room.setStatus(HostelRoom.RoomStatus.FULL);
        }
        roomRepository.save(room);

        return toResponse(allocationRepository.save(allocation));
    }

    public List<AllocationDTO.Response> getAllAllocations() {
        return allocationRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AllocationDTO.Response getAllocationById(Long id) {
        return toResponse(allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", "id", id)));
    }

    public List<AllocationDTO.Response> getActiveAllocations() {
        return allocationRepository.findByStatus(Allocation.AllocationStatus.ACTIVE)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public AllocationDTO.Response getStudentActiveAllocation(Long studentId) {
        Allocation allocation = allocationRepository
                .findByStudentIdAndStatus(studentId, Allocation.AllocationStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Active allocation", "studentId", studentId));
        return toResponse(allocation);
    }

    @Transactional
    public AllocationDTO.Response vacateRoom(Long id) {
        Allocation allocation = allocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", "id", id));

        if (allocation.getStatus() != Allocation.AllocationStatus.ACTIVE) {
            throw new StayloException("Allocation is already " + allocation.getStatus());
        }

        allocation.setStatus(Allocation.AllocationStatus.VACATED);
        allocation.setCheckOutDate(LocalDate.now());

        // Decrement room occupancy
        HostelRoom room = allocation.getRoom();
        room.setOccupied(Math.max(0, room.getOccupied() - 1));
        if (room.getStatus() == HostelRoom.RoomStatus.FULL) {
            room.setStatus(HostelRoom.RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);

        return toResponse(allocationRepository.save(allocation));
    }

    // ---- Mapper ----
    private AllocationDTO.Response toResponse(Allocation allocation) {
        return AllocationDTO.Response.builder()
                .id(allocation.getId())
                .studentId(allocation.getStudent().getId())
                .studentName(allocation.getStudent().getUser().getName())
                .enrollmentNo(allocation.getStudent().getEnrollmentNo())
                .roomId(allocation.getRoom().getId())
                .roomNumber(allocation.getRoom().getRoomNumber())
                .hostelBlock(allocation.getRoom().getHostelBlock())
                .checkInDate(allocation.getCheckInDate())
                .checkOutDate(allocation.getCheckOutDate())
                .status(allocation.getStatus())
                .allocatedBy(allocation.getAllocatedBy())
                .build();
    }
}
