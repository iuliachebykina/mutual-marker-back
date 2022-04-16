package ru.urfu.mutual_marker.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegistrationInfo {
    String username;
    String password;
    String email;
    String phoneNumber;
    String firstName;
    String lastName;
    String patronymic;
    String role;
    String studentGroup;
}
