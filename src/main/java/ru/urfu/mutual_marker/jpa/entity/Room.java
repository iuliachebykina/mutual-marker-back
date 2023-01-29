package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@Where(clause="deleted=false")
public class Room {
    @Id
    @SequenceGenerator(name = "roomSeq", sequenceName = "roomSeq")
    @GeneratedValue(generator = "roomSeq")
    Long id;
    @NotNull
    String title;

    String description;

    @Size(min = 8, max = 8)
    @NotBlank
    @NotNull
    String code;
    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;


    @ManyToMany(mappedBy = "rooms")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    Set<Profile> teachers = new HashSet<>();
    @ManyToMany(mappedBy = "rooms")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    Set<Profile> students = new HashSet<>();
    @OneToMany(mappedBy = "room")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    Set<Task> tasks = new HashSet<>();


    public void addTeacher(Profile teacher){
        if(teachers == null)
            teachers = new HashSet<>();
        teachers.add(teacher);
        teacher.addRoom(this);
    }

    public void addStudent(Profile student){
        if(students == null)
            students = new HashSet<>();
        students.add(student);
        student.addRoom(this);
    }

    public void addTask(Task task){
        if(tasks == null)
            tasks = new HashSet<>();
        tasks.add(task);
    }

    public void removeTeacher(long teacherId) {
        if(teachers == null)
            return;
        Profile teacher = this.teachers.stream().filter(m -> m.getId() == teacherId).findFirst().orElse(null);
        if (teacher != null) {
            this.teachers.remove(teacher);
            teacher.getRooms().remove(this);
        }
    }


    public void removeStudent(long studentId) {
        if(students == null)
            return;
        Profile student = this.students.stream().filter(m -> m.getId() == studentId).findFirst().orElse(null);
        if (student != null) {
            this.students.remove(student);
            student.getRooms().remove(this);
        }
    }

    public void removeTask(long taskId){
        if(tasks == null)
            return;
        this.tasks.stream().filter(a -> a.getId() == taskId).findFirst().ifPresent(task -> this.tasks.remove(task));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Room room = (Room) o;
        return id != null && Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
