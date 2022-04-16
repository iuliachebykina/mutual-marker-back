package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
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
public class MarkStep {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @OneToOne
    Profile owner;
    @Column(length = 50)
    @NotNull
    String title;
    @Column(length = 1000)
    @NotNull
    String description;
    @NotNull
    Integer maxMark;
    @JsonIgnore
    @ManyToMany
    @ToString.Exclude
    List<Task> tasks;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MarkStep markStep = (MarkStep) o;
        return id != null && Objects.equals(id, markStep.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
