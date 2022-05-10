package ru.urfu.mutual_marker.security.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class UserNotExistingException extends MutualMarkerApiException {
    public UserNotExistingException(String message){
        super(message);
    }

}
