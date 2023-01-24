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

    @Query(value = "select t.* from {h-schema}task t where t.min_graded <= " +
            "(select count(*) from mark m where m.owner_id = :profileId " +
            "and m.task_id in (select t2.id from task t2 where t2.room_id = :roomId))",
    nativeQuery = true)
    List<Task> findCompletedByRoom(@Param("roomId") Long roomId, @Param("profileId") Long profileId, Pageable pageable);

    //Второй вариант для рассчёта того, сколько работ осталось оценить
//    @Query(value = "SELECT task.minNumberOfGraded FROM Task task WHERE task.id= :taskId")
//    Long getMinNumberOfGradedForTask(@Param("taskId") Long taskId);
}
