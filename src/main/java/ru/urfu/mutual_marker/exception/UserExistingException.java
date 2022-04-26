package ru.urfu.mutual_marker.exception;

public class UserExistingException extends RuntimeException{
    public UserExistingException(String message){
        super(message);
    }

}
