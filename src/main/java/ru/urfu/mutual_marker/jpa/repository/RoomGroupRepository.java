package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.RoomGroup;

import java.util.List;
import java.util.Optional;

public interface RoomGroupRepository extends JpaRepository<RoomGroup, Long> {
    List<RoomGroup> findByProfile_IdAndDeletedIsFalse(Long id, Pageable pageable);
    Optional<RoomGroup> findByIdAndDeletedIsFalse(Long id);
}