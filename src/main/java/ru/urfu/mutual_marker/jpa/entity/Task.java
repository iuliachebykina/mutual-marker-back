package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    @ManyToOne
    Room room;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

    @OneToMany(mappedBy = "task")
    @ToString.Exclude
    Set<Project> projects = new HashSet<>();
    @ManyToMany(mappedBy = "tasks")
    @ToString.Exclude
    Set<MarkStep> markSteps = new HashSet<>();


    public void addProject(Project project){
        if(projects == null)
            projects = new HashSet<>();
        projects.add(project);
    }

    public void removeProject(long projectId){
        this.projects.stream().filter(a -> a.getId() == projectId).findFirst().ifPresent(project -> this.projects.remove(project));
    }


    public void addMarkStep(MarkStep markStep){
        if(markSteps == null)
            markSteps = new HashSet<>();
        markSteps.add(markStep);
        markStep.getTasks().add(this);
    }

    public void removeMarkStep(long markStepId) {
        MarkStep markStep = this.markSteps.stream().filter(m -> m.getId() == markStepId).findFirst().orElse(null);
        if (markStep != null) {
            this.markSteps.remove(markStep);
            markStep.getTasks().remove(this);
        }
    }

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
