package ru.urfu.mutual_marker.service.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String reason) {
        super(reason);
    }
}
