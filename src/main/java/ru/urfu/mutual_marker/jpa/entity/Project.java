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
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    Profile student;
    @ManyToOne
    Task task;
    @Column(length = 100)
    @NotNull
    String title;
    @Column(length = 2000)
    @NotNull
    String description;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;


    @OneToMany(mappedBy = "project")
    @ToString.Exclude
    Set<Mark> marks = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "project_attachments",
            schema = "mutual_marker",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id"))
    @ToString.Exclude
    Set<Attachment> attachments = new HashSet<>();


    public void addMark(Mark mark){
        if(marks == null)
            marks = new HashSet<>();
        marks.add(mark);
    }

    public void removeMark(long markId){
        if(marks == null)
            return;
        this.marks.stream().filter(a -> a.getId() == markId).findFirst().ifPresent(mark -> this.marks.remove(mark));

    }

    public void addAttachment(Attachment attachment){
        if(attachments == null)
            attachments = new HashSet<>();
        attachments.add(attachment);
        attachment.addProject(this);
    }

    public void removeAttachment(long attachmentId) {
        if(attachments == null)
            return;
        Attachment attachment = this.attachments.stream().filter(a -> a.getId() == attachmentId).findFirst().orElse(null);
        if (attachment != null) {
            this.attachments.remove(attachment);
            attachment.getProjects().remove(this);
        }
    }


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
