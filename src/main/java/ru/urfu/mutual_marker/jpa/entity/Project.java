package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    Profile student;
    @ManyToOne
    Room room;
    @OneToMany(mappedBy = "project")
    Set<Mark> mark;
    @ManyToMany
    Set<Attachment> attachments;
    @Column(length = 100)
    @NotNull
    String title;
    @Column(length = 2000)
    @NotNull
    String description;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;
}
