package ru.urfu.mutual_marker.security.exception;

public class UserExistingException extends RuntimeException{
    public UserExistingException(String message){
        super(message);
    }

}
