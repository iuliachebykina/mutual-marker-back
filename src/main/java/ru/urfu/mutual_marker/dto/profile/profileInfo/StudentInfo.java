package ru.urfu.mutual_marker.dto.profile.profileInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class StudentInfo extends ProfileInfo {
    String studentGroup;
    String university;
    String institute;
}
