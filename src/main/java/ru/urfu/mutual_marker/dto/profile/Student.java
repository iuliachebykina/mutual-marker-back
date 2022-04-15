package ru.urfu.mutual_marker.dto.profile;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Student extends Profile {
    String studentGroup;
}
