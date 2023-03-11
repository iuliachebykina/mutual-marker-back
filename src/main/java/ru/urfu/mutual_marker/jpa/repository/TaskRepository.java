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

    List<Task> findAllByRoom_IdAndDeletedIsFalse(Long roomId, Pageable pageable);

    @Query(value = "select t.* from {h-schema}task t " +
            "left join {h-schema}room r on r.id = t.room_id " +
            "" +
            "    where t.min_graded <= " +
            "      (select count(*) from {h-schema}mark m " +
            "       where m.owner_id = :profileId and m.task_id = t.id" +
            "    ) " +
            "and t.room_id = :roomId " +
            "and r.deleted = false ",
    nativeQuery = true)
    List<Task> findCompletedTask(@Param("roomId") Long roomId, @Param("profileId") Long profileId, Pageable pageable);


    @Query(value = "select t.* from {h-schema}task t " +
            "left join {h-schema}room r on r.id = t.room_id " +
            "" +
            "    where t.min_graded > " +
            "      (select count(*) from {h-schema}mark m " +
            "       where m.owner_id = :profileId and m.task_id = t.id "  +
            "    ) " +
            "and t.room_id = :roomId " +
            "and r.deleted = false ",
            nativeQuery = true)
    List<Task> findUncompletedTask(@Param("roomId") Long roomId, @Param("profileId") Long profileId, Pageable pageable);

    //Второй вариант для рассчёта того, сколько работ осталось оценить
//    @Query(value = "SELECT task.minNumberOfGraded FROM Task task WHERE task.id= :taskId")
//    Long getMinNumberOfGradedForTask(@Param("taskId") Long taskId);
}
