package ru.urfu.mutual_marker.dto.profileInfo;

import lombok.Data;

@Data
public class ProfileInfo {
    Long id;
    String username;
    String phoneNumber;
    String email;
    String firstName;
    String lastName;
    String patronymic;
    String socialNetwork;

}
