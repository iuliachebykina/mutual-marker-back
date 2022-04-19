package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
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
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;


    @ManyToMany
    @JoinTable(
            name = "markstep_tasks",
            schema = "mutual_marker",
            joinColumns = @JoinColumn(name = "mark_step_id"),
            inverseJoinColumns = @JoinColumn(name = "task_id"))
    @ToString.Exclude
    Set<Task> tasks = new HashSet<>();


    public void addTask(Task task) {
        if(tasks == null)
            tasks = new HashSet<>();
        tasks.add(task);
    }

    public void removeTask(long taskId){
        this.tasks.stream().filter(a -> a.getId() == taskId).findFirst().ifPresent(task -> this.tasks.remove(task));
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
