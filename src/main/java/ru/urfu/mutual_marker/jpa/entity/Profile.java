package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.Hibernate;
import ru.urfu.mutual_marker.jpa.entity.value_type.Name;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
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
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String username;
    @Email
    @NotNull
    String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    String password;
    @Enumerated(value = EnumType.STRING)
    Role role;
    @NotNull
    @Embedded
    Name name;
    String studentGroup;
    String phoneNumber;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;


    @OneToMany(mappedBy = "student")
    @ToString.Exclude
    Set<Attachment> attachments = new HashSet<>();
    @ManyToMany
    @JoinTable(
            name = "profile_rooms",
            schema = "mutual_marker",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    @ToString.Exclude
    Set<Room> rooms = new HashSet<>();


    public void addAttachment(Attachment attachment){
        if(attachments == null)
            attachments = new HashSet<>();
        attachments.add(attachment);
    }

    public void addRoom(Room room){
        if(rooms == null)
            rooms = new HashSet<>();
        rooms.add(room);
    }

    public void removeAttachment(long attachmentId){
        this.attachments.stream().filter(a -> a.getId() == attachmentId).findFirst().ifPresent(attachment -> this.attachments.remove(attachment));
    }

    public void removeRoom(long roomId){
        this.rooms.stream().filter(a -> a.getId() == roomId).findFirst().ifPresent(room -> this.rooms.remove(room));
    }


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
