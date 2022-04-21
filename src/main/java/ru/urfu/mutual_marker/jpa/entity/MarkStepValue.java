package ru.urfu.mutual_marker.jpa.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
@Getter
@Setter
public class MarkStepValue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column
    Integer value;

    @ManyToOne
    @JoinColumn(name = "mark_step_id")
    MarkStep markStep;

    @Column(columnDefinition = "boolean default false")
    Boolean deleted;
}
