package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Mark;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByProjectIdAndStudentId(Long projectId, Long studentId);

    List<Mark> findAllByStudentId(Long studentId);

    Long countAllByStudentIdAndProjectTaskId(Long studentId, Long taskId);
}
