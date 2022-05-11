package ru.urfu.mutual_marker.service.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class NotFoundException extends MutualMarkerApiException {
    public NotFoundException(String reason) {
        super(reason);
    }
}
