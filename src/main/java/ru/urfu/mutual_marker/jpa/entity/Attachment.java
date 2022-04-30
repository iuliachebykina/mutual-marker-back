package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import ru.urfu.mutual_marker.jpa.entity.value_type.Extension;

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
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String fileName;
    Extension extension;
    @NotNull
    @ManyToOne
    Profile student;
    @Column(columnDefinition = "boolean default false")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Boolean deleted;


    @ManyToMany(mappedBy = "attachments")
    @Builder.Default
    @ToString.Exclude
    Set<Project> projects = new HashSet<>();


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
