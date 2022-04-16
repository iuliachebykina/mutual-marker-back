package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String title;
    @ManyToMany(mappedBy = "rooms")
    @ToString.Exclude
    List<Profile> teachers;
    @ManyToMany(mappedBy = "rooms")
    @ToString.Exclude
    List<Profile> students;
    @Size(min = 8, max = 8)
    @NotBlank
    @NotNull
    String code;
    @OneToMany(mappedBy = "room")
    @ToString.Exclude
    List<Task> tasks;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

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
