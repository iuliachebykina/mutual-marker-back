package ru.urfu.mutual_marker.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationInfo {
    String email;
    String password;
    String phoneNumber;
    String firstName;
    String lastName;
    String patronymic;
    String university;
    String institute;
    String studentGroup;
    String socialNetwork;
    String subject;
}
