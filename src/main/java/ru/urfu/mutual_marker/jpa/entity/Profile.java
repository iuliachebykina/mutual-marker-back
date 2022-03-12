package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.urfu.mutual_marker.jpa.entity.value_type.Name;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Enumerated(value = EnumType.STRING)
    @NotNull
    Role role;
    @NotNull
    @Embedded
    Name name;
    String studentGroup;
    String phoneNumber;
    @ManyToMany
    Set<Room> rooms;
    @OneToMany(mappedBy = "student")
    Set<Mark> marks;
    @OneToMany(mappedBy = "student")
    Set<Attachment> attachments;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;
}
