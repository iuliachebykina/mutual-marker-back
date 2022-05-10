package ru.urfu.mutual_marker.service.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class InvalidArgumentException extends MutualMarkerApiException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}
