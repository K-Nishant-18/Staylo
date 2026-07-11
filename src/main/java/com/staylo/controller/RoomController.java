package com.staylo.controller;

import com.staylo.dto.ApiResponse;
import com.staylo.dto.RoomDTO;
import com.staylo.entity.HostelRoom;
import com.staylo.service.RoomService;
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
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Hostel Rooms", description = "Room management and availability endpoints")
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Add a new room")
    public ResponseEntity<ApiResponse<RoomDTO.Response>> addRoom(
            @Valid @RequestBody RoomDTO.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Room added successfully", roomService.addRoom(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Get all rooms")
    public ResponseEntity<ApiResponse<List<RoomDTO.Response>>> getAllRooms() {
        return ResponseEntity.ok(ApiResponse.success(roomService.getAllRooms()));
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")
    @Operation(summary = "Get all available rooms")
    public ResponseEntity<ApiResponse<List<RoomDTO.Response>>> getAvailableRooms(
            @RequestParam(required = false) HostelRoom.RoomType type) {
        List<RoomDTO.Response> rooms = (type != null)
                ? roomService.getAvailableRoomsByType(type)
                : roomService.getAvailableRooms();
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN', 'STUDENT')")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<ApiResponse<RoomDTO.Response>> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roomService.getRoomById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Update room details")
    public ResponseEntity<ApiResponse<RoomDTO.Response>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomDTO.Request request) {
        return ResponseEntity.ok(ApiResponse.success("Room updated successfully",
                roomService.updateRoom(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WARDEN')")
    @Operation(summary = "Update room status (AVAILABLE, FULL, MAINTENANCE)")
    public ResponseEntity<ApiResponse<RoomDTO.Response>> updateStatus(
            @PathVariable Long id,
            @RequestParam HostelRoom.RoomStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Room status updated",
                roomService.updateRoomStatus(id, status)));
    }
}
