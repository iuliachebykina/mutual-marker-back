package ru.urfu.mutual_marker.jpa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.urfu.mutual_marker.jpa.entity.Task;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByRoom_Id(Long roomId, Pageable pageable);

    @Query(value = "SELECT task.minNumberOfGraded FROM Task task WHERE task.id= :taskId")
    Long getMinNumberOfGradedForTask(@Param("taskId") Long taskId);
}
