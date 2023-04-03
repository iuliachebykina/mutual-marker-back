package ru.urfu.mutual_marker.jpa.repository.mark;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.MarkStep;
import ru.urfu.mutual_marker.jpa.entity.Task;

import java.util.List;
import java.util.Set;

public interface MarkStepRepository extends JpaRepository<MarkStep, Long> {
    List<MarkStep> findAllByTasksIn(Set<Task> tasks);
}
