package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByRole(Role role);
    Profile findByUsername(String username);
}
