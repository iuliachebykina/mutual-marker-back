package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByStudentsEmailAndDeletedIsFalse(String email, Pageable pageable);
    List<Room> findAllByTeachersEmailAndDeletedIsFalse(String email, Pageable pageable);
    List<Room> findAllByStudentsEmailAndDeletedIsFalseAndRoomGroupIsNull(String email, Pageable pageable);
    List<Room> findAllByTeachersEmailAndDeletedIsFalseAndRoomGroupIsNull(String email, Pageable pageable);
    Optional<Room> findByCodeAndDeletedIsFalse(String code);
}