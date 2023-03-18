package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
@Getter
@Setter
@Where(clause="deleted=false")
@ToString
public class MarkStepFeedback {
    @Id
    @SequenceGenerator(name = "markStepFeedbackSeq", sequenceName = "markStepFeedbackSeq")
    @GeneratedValue(generator = "markStepFeedbackSeq")
    Long id;

    @OneToOne
    MarkStep markStep;

    @Column
    String comment;

    @ManyToOne
    Profile owner;

    @Column
    Integer value;

    @JsonIgnore
    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    Boolean deleted = Boolean.FALSE;
}
