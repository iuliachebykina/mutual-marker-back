package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Profile;
import ru.urfu.mutual_marker.jpa.entity.Project;
import ru.urfu.mutual_marker.jpa.entity.Task;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findAllByTask(Task task);

    List<Project> findAllByTask_Id(Long taskId);

    Optional<Project> findByStudentAndTask(Profile student, Task task);

    Optional<Project> findByStudentAndId(Profile student, Long projectId);

    Boolean existsByStudentIdAndTaskId(Long studentId, Long taskId);
}
