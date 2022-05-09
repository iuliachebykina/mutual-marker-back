package ru.urfu.mutual_marker.security.exception;

public class UserNotExistingException extends RuntimeException{
    public UserNotExistingException(String message){
        super(message);
    }

}
