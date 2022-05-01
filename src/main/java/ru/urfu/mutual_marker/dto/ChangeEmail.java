package ru.urfu.mutual_marker.dto;

import lombok.Data;

@Data
public class ChangeEmail {
    String oldEmail;
    String newEmail;
}
