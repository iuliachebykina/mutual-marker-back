package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.NumberOfGraded;

import java.util.List;

public interface NumberOfGradedRepository extends JpaRepository<NumberOfGraded, Long> {
    List<NumberOfGraded> findAllByProfileEmail(String email, Pageable pageable);
}
