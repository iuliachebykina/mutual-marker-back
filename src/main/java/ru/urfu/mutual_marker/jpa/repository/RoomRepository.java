package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.urfu.mutual_marker.jpa.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findAllByStudentsEmailAndDeletedIsFalse(String email, Pageable pageable);
    List<Room> findAllByTeachersEmailAndDeletedIsFalse(String email, Pageable pageable);
    @Query(value = "select r.* from {h-schema}room r " +
            "left join {h-schema}profile_rooms pr on r.id = pr.room_id " +
            "left join {h-schema}profile p on p.id = pr.profile_id " +
            "    where r.id not in (select r1.id from {h-schema}room r1 " +
            "                                          left join {h-schema}roomgroup_rooms rr on r1.id = rr.room_id " +
            "                                          left join {h-schema}room_group rg on rr.roomgroup_id = rg.id " +
            "                                          left join {h-schema}profile p on rg.profile_id = p.id " +
            "                       where p.email = :email) " +
            "        and p.email = :email " +
            "and r.deleted = false " +
            "and p.deleted = false ", nativeQuery = true)
    List<Room> findAllByDeletedIsFalseAndNotInGroup(String email, Pageable pageable);
    Optional<Room> findByCodeAndDeletedIsFalse(String code);
}