package ru.urfu.mutual_marker.service.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class RoomServiceException extends MutualMarkerApiException {
    public RoomServiceException(String message){
        super(message);
    }
}
