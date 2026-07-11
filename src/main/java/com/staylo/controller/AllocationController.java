package com.staylo.controller;

import com.staylo.dto.AllocationDTO;
import com.staylo.dto.ApiResponse;
import com.staylo.service.AllocationService;
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
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Room Allocations", description = "Room allocation and vacating endpoints")
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Allocate a room to a student")
    public ResponseEntity<ApiResponse<AllocationDTO.Response>> allocate(
            @Valid @RequestBody AllocationDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room allocated successfully",
                        allocationService.allocateRoom(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get all allocations")
    public ResponseEntity<ApiResponse<List<AllocationDTO.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(allocationService.getAllAllocations()));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get all currently active allocations")
    public ResponseEntity<ApiResponse<List<AllocationDTO.Response>>> getActive() {
        return ResponseEntity.ok(ApiResponse.success(allocationService.getActiveAllocations()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get allocation by ID")
    public ResponseEntity<ApiResponse<AllocationDTO.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(allocationService.getAllocationById(id)));
    }

    @GetMapping("/student/{studentId}/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")
    @Operation(summary = "Get student's current active allocation")
    public ResponseEntity<ApiResponse<AllocationDTO.Response>> getStudentActiveAllocation(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(
                allocationService.getStudentActiveAllocation(studentId)));
    }

    @PutMapping("/{id}/vacate")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Vacate a room (mark allocation as VACATED)")
    public ResponseEntity<ApiResponse<AllocationDTO.Response>> vacate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Room vacated successfully",
                allocationService.vacateRoom(id)));
    }
}
