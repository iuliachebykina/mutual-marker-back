package ru.urfu.mutual_marker.jpa.entity;

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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    Profile student;
    @ManyToOne
    Task task;
    @OneToMany(mappedBy = "project")
    @ToString.Exclude
    List<Mark> mark;
    @ManyToMany
    @JoinTable(
            name = "project_attachment",
            schema = "mutual_marker",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    @ToString.Exclude
    List<Attachment> attachments;
    @Column(length = 100)
    @NotNull
    String title;
    @Column(length = 2000)
    @NotNull
    String description;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Project project = (Project) o;
        return id != null && Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
