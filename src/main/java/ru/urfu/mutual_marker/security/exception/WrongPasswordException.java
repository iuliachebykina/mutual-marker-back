package ru.urfu.mutual_marker.security.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class WrongPasswordException extends MutualMarkerApiException {
    public WrongPasswordException(String message){
        super(message);
    }

}

