package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import ru.urfu.mutual_marker.jpa.entity.value_type.Name;
import ru.urfu.mutual_marker.jpa.entity.value_type.Role;

import javax.persistence.*;
import javax.validation.constraints.Email;
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

public class Profile {
    @Id
    @SequenceGenerator(name = "profileSeq", sequenceName = "profileSeq", allocationSize = 1)
    @GeneratedValue(generator = "profileSeq")
    Long id;
    @Email
    @NotNull
    String email;
    @JsonIgnore
    @NotNull
    String password;
    @Enumerated(value = EnumType.STRING)
    Role role;
    @NotNull
    @Embedded
    Name name;
    String subject;
    String university;
    String institute;
    String studentGroup;
    String phoneNumber;
    String socialNetwork;
    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    @OneToMany(mappedBy = "student")
    @JsonIgnore
    @Builder.Default
    @ToString.Exclude
    Set<Attachment> attachments = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    @JoinTable(
            name = "profile_rooms",
            schema = "mutual_marker",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    @Builder.Default
    @ToString.Exclude
    Set<Room> rooms = new HashSet<>();

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    @JsonIgnore
    @Builder.Default
    @ToString.Exclude
    Set<MarkStepFeedback> markStepFeedbacks = new HashSet<>();

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

    public void addMarkStepFeedback(MarkStepFeedback markStepFeedback){
        if (markStepFeedbacks == null){
            markStepFeedbacks = new HashSet<>();
        }
        markStepFeedbacks.add(markStepFeedback);
    }

    public void removeAttachment(long attachmentId){
        if(attachments == null)
            return;
        this.attachments.stream().filter(a -> a.getId() == attachmentId).findFirst().ifPresent(attachment -> this.attachments.remove(attachment));
    }

    public void removeRoom(long roomId){
        if(rooms == null)
            return;
        this.rooms.stream().filter(a -> a.getId() == roomId).findFirst().ifPresent(room -> this.rooms.remove(room));
    }

    public void removeMarkStepFeedback(long markStepFeedbackId){
        if (markStepFeedbacks == null){
            return;
        }
        this.markStepFeedbacks.removeIf(markStepFeedback -> markStepFeedback.getId() == markStepFeedbackId);
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
