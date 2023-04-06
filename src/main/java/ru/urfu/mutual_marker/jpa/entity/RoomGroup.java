package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")

public class RoomGroup {
    @Id
    @SequenceGenerator(name = "roomGroupSeq", sequenceName = "roomGroupSeq")
    @GeneratedValue(generator = "roomGroupSeq")
    Long id;
    @NotNull
    String title;
    @ManyToOne
    Profile profile;
    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    @ManyToMany
    @JoinTable(
            name = "roomgroup_rooms",
            schema = "mutual_marker",
            joinColumns = @JoinColumn(name = "roomgroup_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    @Builder.Default
    @ToString.Exclude
    @JsonIgnore
    Set<Room> rooms = new HashSet<>();


    public void addRoom(Room room){
        if(rooms == null)
            rooms = new HashSet<>();
        rooms.add(room);

    }


    public void removeRoom(Room room){
        if(rooms == null)
            return;
        this.rooms.stream().filter(a -> Objects.equals(a.getId(), room.getId())).findFirst().ifPresent(r -> this.rooms.remove(r));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoomGroup room = (RoomGroup) o;
        return id != null && Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
