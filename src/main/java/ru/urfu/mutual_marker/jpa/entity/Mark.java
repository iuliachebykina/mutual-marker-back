package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
@Where(clause="deleted=false")
@EntityListeners(AuditingEntityListener.class)
public class Mark {
    @Id
    @SequenceGenerator(name = "markSeq", sequenceName = "markSeq")
    @GeneratedValue(generator = "markSeq")
    Long id;
    @ManyToOne
    Project project;
    @ManyToOne
    Profile owner;
    Double coefficient;
    @Builder.Default
    Boolean isTeacherMark = Boolean.FALSE;
    @NotNull
    Integer markValue;
    @CreatedDate
    Date markTime;
    String comment;
    Long taskId;
    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Mark mark = (Mark) o;
        return id != null && Objects.equals(id, mark.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
