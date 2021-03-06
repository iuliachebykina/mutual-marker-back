package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByRole(Role role, Pageable pageable);
    Optional<Profile> findByEmail(String email);
    Optional<Profile> getByEmail(String email);
    List<Profile> findAllByRoomsIdAndRole(Long roomId, Role role, Pageable pageable);
}
