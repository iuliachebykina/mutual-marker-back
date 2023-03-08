package ru.urfu.mutual_marker.security.jwt.jpa;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(schema = "mutual_marker")
@Where(clause="deleted=false")
public class Token {
    @Id
    String login;
    String token;
    @JsonIgnore
    @Builder.Default
    Boolean deleted = Boolean.FALSE;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Token token = (Token) o;
        return login != null && Objects.equals(login, token.login);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
