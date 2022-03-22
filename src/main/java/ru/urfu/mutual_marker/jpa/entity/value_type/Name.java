package ru.urfu.mutual_marker.jpa.entity.value_type;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Column;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Name {
    @Column(length = 50)
    @NotNull
    String firstName;
    @Column(length = 50)
    @NotNull
    String lastName;
    @Column(length = 50)
    String patronymic;

}
