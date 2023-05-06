package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

public class MarkStep {
    @Id
    @SequenceGenerator(name = "markStepSeq", sequenceName = "markStepSeq", allocationSize = 1)
    @GeneratedValue(generator = "markStepSeq")
    Long id;
    @ManyToOne
    Profile owner;
    @Column(length = 50)
    @NotNull
    String title;
    @Column(length = 1000)
    @NotNull
    String description;

    @JsonIgnore
    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    Boolean deleted = Boolean.FALSE;


    @ManyToMany
    @JoinTable(
            name = "markstep_tasks",
            schema = "mutual_marker",
            joinColumns = @JoinColumn(name = "mark_step_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "markStep", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Builder.Default
    Set<MarkStepValue> values = new HashSet<>();

    @OneToMany(mappedBy = "markStep")
    @ToString.Exclude
    @Builder.Default
    Set<MarkStepFeedback> markStepFeedbacks = new HashSet<>();


    public void markStepFeedback(MarkStepFeedback markStepFeedback) {
        if(markStepFeedbacks == null)
            markStepFeedbacks = new HashSet<>();
        markStepFeedbacks.add(markStepFeedback);
    }

    public void removeMarkStepFeedback(long markStepFeedbackId){
        if(markStepFeedbacks == null)
            return;
        this.markStepFeedbacks.stream().filter(a -> a.getId() == markStepFeedbackId).findFirst().ifPresent(msf -> this.markStepFeedbacks.remove(msf));
    }

    public void addTask(Task task) {
        if(tasks == null)
            tasks = new HashSet<>();
        tasks.add(task);
    }

    public void removeTask(long taskId){
        if(tasks == null)
            return;
        this.tasks.stream().filter(a -> a.getId() == taskId).findFirst().ifPresent(task -> this.tasks.remove(task));
    }

    public void addValue(MarkStepValue value) {
        if(values == null)
            values = new HashSet<>();
        values.add(value);
    }

    public void removeValue(long valueId){
        if(values == null)
            return;
        this.values.stream().filter(a -> a.getId() == valueId).findFirst().ifPresent(value -> this.values.remove(value));
    }

    public void delete() {
        this.deleted = true;
        this.values.forEach(MarkStepValue::delete);
    }


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
