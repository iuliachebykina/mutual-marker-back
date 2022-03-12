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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToMany(mappedBy = "rooms")
    Set<Profile> teachers;
    @ManyToMany(mappedBy = "rooms")
    Set<Profile> students;
    @Column(length = 2000)
    String description;
    @NotNull
    Date openDate;
    @NotNull
    Date closeDate;
    @OneToMany(mappedBy = "room")
    Set<Project> project;
    @ManyToMany(mappedBy = "rooms")
    Set<MarkStep> markSteps;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

}
