package ru.urfu.mutual_marker.jpa.entity;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.urfu.mutual_marker.jpa.entity.value_type.Extension;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @NotNull
    String fileName;
    Extension extension;
    @ManyToMany(mappedBy = "attachments")
    Set<Project> projects;
    @NotNull
    @ManyToOne
    Profile student;
    @Column(columnDefinition = "boolean default false")
    Boolean deleted;
}
