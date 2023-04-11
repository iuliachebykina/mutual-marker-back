package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
public class MarkStepFeedback {
    @Id
    @SequenceGenerator(name = "markStepFeedbackSeq", sequenceName = "markStepFeedbackSeq")
    @GeneratedValue(generator = "markStepFeedbackSeq")
    Long id;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    MarkStep markStep;

    @Column
    String comment;

    @NotNull
    @ManyToOne
    Profile owner;

    @Column
    Integer value;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    Mark mark;

    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;
}
