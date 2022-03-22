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
@Table(schema = "mutual_marker")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String title;
    @ManyToMany(mappedBy = "rooms")
    Set<Profile> teachers;
    @ManyToMany(mappedBy = "rooms")
    Set<Profile> students;
    @OneToMany
    Set<Task> tasks;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

}
