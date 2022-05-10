package ru.urfu.mutual_marker.security.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class UserExistingException extends MutualMarkerApiException {
    public UserExistingException(String message) {
        super(message);
    }

}
