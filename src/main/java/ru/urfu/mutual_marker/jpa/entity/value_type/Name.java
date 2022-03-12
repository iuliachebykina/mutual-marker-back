package ru.urfu.mutual_marker.jpa.entity.value_type;

import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Name {
    @NotNull
    String firstName;
    @NotNull
    String lastName;
    String patronymic;

}
