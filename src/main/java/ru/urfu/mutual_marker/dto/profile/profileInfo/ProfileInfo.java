package ru.urfu.mutual_marker.dto.profile.profileInfo;

import lombok.Data;

@Data
public class ProfileInfo {
    Long id;
    String email;
    String phoneNumber;
    String firstName;
    String lastName;
    String patronymic;
    String socialNetwork;

}
