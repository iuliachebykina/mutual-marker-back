package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import ru.urfu.mutual_marker.jpa.entity.value_type.Name;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
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
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Email
    @NotNull
    String email;
    @NotNull
    String password;
    @Enumerated(value = EnumType.STRING)
    @NotNull
    Role role;
    @NotNull
    @Embedded
    Name name;
    String studentGroup;
    String phoneNumber;
    @ManyToMany
    @ToString.Exclude
    Set<Room> rooms;
    @OneToMany(mappedBy = "student")
    @ToString.Exclude
    Set<Mark> marks;
    @OneToMany(mappedBy = "student")
    @ToString.Exclude
    Set<Attachment> attachments;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Profile profile = (Profile) o;
        return id != null && Objects.equals(id, profile.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
