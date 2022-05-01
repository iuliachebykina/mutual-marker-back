package ru.urfu.mutual_marker.exception;

public class UserNotExistingException extends RuntimeException{
    public UserNotExistingException(String message){
        super(message);
    }

}
