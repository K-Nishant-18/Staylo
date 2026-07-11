package com.staylo.repository;

import com.staylo.entity.HostelRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HostelRoomRepository extends JpaRepository<HostelRoom, Long> {
    Optional<HostelRoom> findByRoomNumber(String roomNumber);
    List<HostelRoom> findByHostelBlock(String hostelBlock);
    List<HostelRoom> findByStatus(HostelRoom.RoomStatus status);
    List<HostelRoom> findByType(HostelRoom.RoomType type);

    @Query("SELECT r FROM HostelRoom r WHERE r.occupied < r.capacity AND r.status = 'AVAILABLE'")
    List<HostelRoom> findAvailableRooms();

    @Query("SELECT r FROM HostelRoom r WHERE r.occupied < r.capacity AND r.status = 'AVAILABLE' AND r.type = :type")
    List<HostelRoom> findAvailableRoomsByType(HostelRoom.RoomType type);
}
