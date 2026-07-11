package com.staylo.service;

import com.staylo.dto.RoomDTO;
import com.staylo.entity.HostelRoom;
import com.staylo.exception.ResourceNotFoundException;
import com.staylo.exception.StayloException;
import com.staylo.repository.HostelRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final HostelRoomRepository roomRepository;

    @Transactional
    public RoomDTO.Response addRoom(RoomDTO.Request request) {
        if (roomRepository.findByRoomNumber(request.getRoomNumber()).isPresent()) {
            throw new StayloException("Room number already exists: " + request.getRoomNumber());
        }

        HostelRoom room = HostelRoom.builder()
                .roomNumber(request.getRoomNumber())
                .hostelBlock(request.getHostelBlock())
                .floor(request.getFloor())
                .type(request.getType())
                .capacity(request.getCapacity())
                .monthlyFee(request.getMonthlyFee())
                .hasAttachedBathroom(request.isHasAttachedBathroom())
                .hasAc(request.isHasAc())
                .build();

        return toResponse(roomRepository.save(room));
    }

    public List<RoomDTO.Response> getAllRooms() {
        return roomRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RoomDTO.Response> getAvailableRooms() {
        return roomRepository.findAvailableRooms().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<RoomDTO.Response> getAvailableRoomsByType(HostelRoom.RoomType type) {
        return roomRepository.findAvailableRoomsByType(type).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public RoomDTO.Response getRoomById(Long id) {
        return toResponse(roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id)));
    }

    @Transactional
    public RoomDTO.Response updateRoom(Long id, RoomDTO.Request request) {
        HostelRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));

        room.setHostelBlock(request.getHostelBlock());
        room.setFloor(request.getFloor());
        room.setType(request.getType());
        room.setCapacity(request.getCapacity());
        room.setMonthlyFee(request.getMonthlyFee());
        room.setHasAttachedBathroom(request.isHasAttachedBathroom());
        room.setHasAc(request.isHasAc());

        return toResponse(roomRepository.save(room));
    }

    @Transactional
    public RoomDTO.Response updateRoomStatus(Long id, HostelRoom.RoomStatus status) {
        HostelRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "id", id));
        room.setStatus(status);
        return toResponse(roomRepository.save(room));
    }

    // ---- Mapper ----
    private RoomDTO.Response toResponse(HostelRoom room) {
        return RoomDTO.Response.builder()
                .id(room.getId())
                .roomNumber(room.getRoomNumber())
                .hostelBlock(room.getHostelBlock())
                .floor(room.getFloor())
                .type(room.getType())
                .capacity(room.getCapacity())
                .occupied(room.getOccupied())
                .monthlyFee(room.getMonthlyFee())
                .hasAttachedBathroom(room.isHasAttachedBathroom())
                .hasAc(room.isHasAc())
                .status(room.getStatus())
                .available(room.isAvailable())
                .build();
    }
}
