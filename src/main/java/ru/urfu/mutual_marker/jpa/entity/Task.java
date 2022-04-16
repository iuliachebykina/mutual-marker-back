package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String title;
    @Column(length = 2000)
    String description;
    @NotNull
    LocalDateTime openDate;
    @NotNull
    LocalDateTime closeDate;
    @JsonIgnore
    @OneToMany(mappedBy = "task")
    @ToString.Exclude
    List<Project> project;
    @ManyToOne
    Room room;
    @JsonIgnore
    @ManyToMany(mappedBy = "tasks")
    @ToString.Exclude
    List<MarkStep> markSteps;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Task task = (Task) o;
        return id != null && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
