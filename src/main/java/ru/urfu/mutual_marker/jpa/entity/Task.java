package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
public class Task {
    @Id
    @SequenceGenerator(name = "taskSeq", sequenceName = "taskSeq", allocationSize = 1)
    @GeneratedValue(generator = "taskSeq")
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
    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    @Column(name = "min_graded")
    Integer minNumberOfGraded;

    @Column(name = "max_grade")
    Double maxGrade;

    @OneToMany(mappedBy = "task")
    @Builder.Default
    @ToString.Exclude
    Set<Attachment> attachments = new HashSet<>();

    @OneToMany(mappedBy = "task")
    @Builder.Default
    @JsonIgnore
    @ToString.Exclude
    Set<Project> projects = new HashSet<>();

    @ManyToMany(mappedBy = "tasks")
    @Builder.Default
    @ToString.Exclude
    Set<MarkStep> markSteps = new HashSet<>();

    public void addProject(Project project){
        if(projects == null)
            projects = new HashSet<>();
        projects.add(project);
    }

    public void removeProject(long projectId){
        if(projects == null)
            return;
        this.projects.stream().filter(a -> a.getId() == projectId).findFirst().ifPresent(project -> this.projects.remove(project));
    }

    public void addMarkStep(MarkStep markStep){
        if(markSteps == null)
            markSteps = new HashSet<>();
        markSteps.add(markStep);
        markStep.addTask(this);
    }

    public void removeMarkStep(long markStepId) {
        if(markSteps == null)
            return;
        MarkStep markStep = this.markSteps.stream().filter(m -> m.getId() == markStepId).findFirst().orElse(null);
        if (markStep != null) {
            this.markSteps.remove(markStep);
            markStep.getTasks().remove(this);
        }
    }

    public void addAttachment(Attachment attachment){
        if(attachments == null)
            attachments = new HashSet<>();
        attachments.add(attachment);
        attachment.setTask(this);
    }


    public void delete() {
        this.deleted = true;
        this.markSteps.forEach(MarkStep::delete);
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
