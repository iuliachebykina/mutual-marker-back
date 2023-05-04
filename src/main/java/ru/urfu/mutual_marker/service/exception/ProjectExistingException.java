package ru.urfu.mutual_marker.service.exception;

import ru.urfu.mutual_marker.exception.MutualMarkerApiException;

public class ProjectExistingException extends MutualMarkerApiException {
    public ProjectExistingException(String message){
        super(message);
    }
}
