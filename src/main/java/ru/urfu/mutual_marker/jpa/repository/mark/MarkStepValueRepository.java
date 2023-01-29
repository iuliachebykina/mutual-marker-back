package ru.urfu.mutual_marker.jpa.repository.mark;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.urfu.mutual_marker.jpa.entity.MarkStepValue;

@Repository
public interface MarkStepValueRepository extends JpaRepository<MarkStepValue, Long> {
}
