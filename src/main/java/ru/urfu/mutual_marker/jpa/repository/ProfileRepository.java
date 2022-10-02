package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByRole(Role role, Pageable pageable);
    Optional<Profile> findByEmail(String email);
    Optional<Profile> getByEmail(String email);
    List<Profile> findAllByRoomsIdAndRole(Long roomId, Role role, Pageable pageable);
    Long countByRoomsId(Long roomId);
    List<Profile> findByEmailContainingIgnoreCaseOrNameFirstNameContainingIgnoreCaseOrNameLastNameContainingIgnoreCaseOrNamePatronymicContainingIgnoreCaseAndRole(@Email @NotNull String email,
                                                                                                                                                           @NotNull String firstName, String lastName, String patronymic, Pageable pageable, Role role);
}
