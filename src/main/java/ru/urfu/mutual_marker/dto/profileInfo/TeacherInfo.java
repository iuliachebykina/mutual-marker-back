package ru.urfu.mutual_marker.dto.profileInfo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherInfo extends ProfileInfo {
    String subject;
    String university;
    String institute;
}
