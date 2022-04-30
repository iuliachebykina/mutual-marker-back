package ru.urfu.mutual_marker.jpa.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
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
public class Mark {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @ManyToOne
    Project project;
    @ManyToOne
    Profile student;
    @NotNull
    Integer markValue;
    @NotNull
    @CreatedDate
    LocalDateTime markTime;
    String comment;
    @Column(columnDefinition = "boolean default false")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Boolean deleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Mark mark = (Mark) o;
        return id != null && Objects.equals(id, mark.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
