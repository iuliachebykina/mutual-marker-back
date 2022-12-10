package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;

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
@Where(clause="deleted=false")
public class Attachment {
    @Id
    @SequenceGenerator(name = "attachmentSeq", sequenceName = "attachmentSeq")
    @GeneratedValue(generator = "attachmentSeq")
    Long id;
    @NotNull
    String fileName;
    String contentType;
    @NotNull
    @ManyToOne
    Profile student;

    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;


    @ManyToMany(mappedBy = "attachments")
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    Set<Project> projects = new HashSet<>();

    @ManyToOne
    @ToString.Exclude
//    @JsonIgnore
    Task task;

    public void addProject(Project project){
        if(projects == null) //  если собирать с помощью билдера, сеты остаются не инициализированными, пусть на всякий случай эта проверка остается
            projects = new HashSet<>();
        projects.add(project);
    }

    public void removeProject(long projectId){
        if(projects == null)
            return;
        this.projects.stream().filter(a -> a.getId() == projectId).findFirst().ifPresent(project -> this.projects.remove(project));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Attachment that = (Attachment) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
