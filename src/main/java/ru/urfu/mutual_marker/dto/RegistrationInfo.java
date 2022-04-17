package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationInfo {
    String username;
    String password;
    String email;
    String phoneNumber;
    String firstName;
    String lastName;
    String patronymic;
    String studentGroup;
}
