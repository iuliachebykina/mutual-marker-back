package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByTaskAndDeletedIsFalse(Task task);

    List<Project> findAllByTask_IdAndDeletedIsFalse(Long taskId);

    Optional<Project> findByStudentAndTaskAndDeletedIsFalse(Profile student, Task task);

    Optional<Project> findByStudentIdAndTaskIdAndDeletedIsFalse(Long studentId, Long taskId);

    Optional<Project> findByStudentAndIdAndDeletedIsFalse(Profile student, Long projectId);

    Boolean existsByStudentIdAndTaskIdAndDeletedIsFalse(Long studentId, Long taskId);
}
