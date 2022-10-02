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
public class MarkStepValue {
    @Id
    @SequenceGenerator(name = "markStepValueSeq", sequenceName = "markStepValueSeq")
    @GeneratedValue(generator = "markStepValueSeq")
    Long id;

    @Column
    Integer value;

    @ManyToOne
    @JsonIgnore
    MarkStep markStep;

    @JsonIgnore
    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    Boolean deleted = Boolean.FALSE;

    public MarkStepValue(Integer value) {
        this.value = value;
        this.deleted = false;
    }

    public void delete() {
        this.deleted = true;
    }
}
