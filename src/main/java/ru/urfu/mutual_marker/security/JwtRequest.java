package ru.urfu.mutual_marker.security;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JwtRequest {

    private String login;
    private String role;
    private String password;

}