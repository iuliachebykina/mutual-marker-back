package ru.urfu.mutual_marker.security.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class InvalidRoleException extends MutualMarkerApiException {
    public InvalidRoleException(String message) {
        super(message);
    }

}
