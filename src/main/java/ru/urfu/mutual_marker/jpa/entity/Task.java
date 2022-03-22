package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
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
    Date openDate;
    @NotNull
    Date closeDate;
    @OneToMany(mappedBy = "task")
    Set<Project> project;
    @ManyToMany(mappedBy = "tasks")
    Set<MarkStep> markSteps;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;
}