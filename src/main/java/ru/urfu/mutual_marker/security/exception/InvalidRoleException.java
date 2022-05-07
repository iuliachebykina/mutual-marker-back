package ru.urfu.mutual_marker.security.exception;

public class InvalidRoleException extends RuntimeException{
    public InvalidRoleException(String message){
        super(message);
    }

}
