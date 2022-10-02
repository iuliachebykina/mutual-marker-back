package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Table(schema = "mutual_marker")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@Where(clause="deleted=false")
public class NumberOfGraded {
    @Id
    @SequenceGenerator(name = "numberOfGradedSeq", sequenceName = "numberOfGradedSeq")
    @GeneratedValue(generator = "numberOfGradedSeq")
    Long id;

    @ManyToOne
    Task task;

    @ManyToOne
    Profile profile;

    Integer graded;

    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;
}
