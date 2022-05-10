package ru.urfu.mutual_marker.service.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class NumberOfGradedServiceException extends MutualMarkerApiException {
    public NumberOfGradedServiceException(String message){
        super(message);
    }
}
