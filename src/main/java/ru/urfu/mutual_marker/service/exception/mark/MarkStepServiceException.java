package ru.urfu.mutual_marker.service.exception.mark;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class MarkStepServiceException extends MutualMarkerApiException {
    public MarkStepServiceException(String message){
        super(message);
    }
}
