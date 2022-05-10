package ru.urfu.mutual_marker.service.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class MarkServiceException extends MutualMarkerApiException {
    public MarkServiceException(String message){
        super(message);
    }
}
