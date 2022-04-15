package ru.urfu.mutual_marker.dto.profile;

import lombok.Data;

@Data
public class Profile {
    Long id;
    String email;
    String phoneNumber;
    String firstName;
    String lastName;
    String patronymic;
}
