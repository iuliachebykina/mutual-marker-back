package ru.urfu.mutual_marker.dto;

import lombok.Data;

@Data
public class ChangePassword {
    String email;
    String oldPassword;
    String newPassword;
}
