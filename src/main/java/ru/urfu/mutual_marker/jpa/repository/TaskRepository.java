package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.urfu.mutual_marker.jpa.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
