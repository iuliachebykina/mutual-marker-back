package ru.urfu.mutual_marker.jpa.repository.mark;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Mark;

import java.util.List;
import java.util.Optional;

public interface MarkRepository extends JpaRepository<Mark, Long> {
    Optional<Mark> findByProjectIdAndOwnerId(Long projectId, Long studentId);

    List<Mark> findAllByOwnerId(Long studentId);

    Long countAllByOwnerIdAndProjectTaskId(Long studentId, Long taskId);
}
