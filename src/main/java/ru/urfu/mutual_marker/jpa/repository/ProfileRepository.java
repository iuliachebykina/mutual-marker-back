package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByRoleAndDeletedIsFalse(Role role, Pageable pageable);
    Optional<Profile> findByEmailAndDeletedIsFalse(String email);
    Optional<Profile> getByEmailAndDeletedIsFalse(String email);
    List<Profile> findAllByRoomsIdAndRoleAndDeletedIsFalse(Long roomId, Role role, Pageable pageable);
    Long countByRoomsIdAndDeletedIsFalse(Long roomId);
    @Query(value = "SELECT p FROM Profile p WHERE (lower(p.email) like lower(concat('%',:email,'%')) " +
            "OR lower(p.name.firstName) like lower(concat('%',:firstname,'%'))  " +
            "OR lower(p.name.lastName) like lower(concat('%',:lastname,'%'))  " +
            "OR lower(p.name.patronymic) like lower(concat('%',:patronymic,'%')))  " +
            "AND p.deleted = false" +
            "AND p.role=:role",
    nativeQuery = true)
    List<Profile> findByNameEmailOrPatronymicIgnoreCase(@Email @NotNull @Param("email") String email,
                                                        @NotNull @Param("firstname") String firstName,
                                                        @Param("lastname") String lastName,
                                                        @Param("patronymic") String patronymic,
                                                        Pageable pageable, @Param("role") Role role);
}
