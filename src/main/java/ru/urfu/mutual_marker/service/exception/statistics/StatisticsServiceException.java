package ru.urfu.mutual_marker.service.exception.statistics;

public class StatisticsServiceException extends RuntimeException{
    public StatisticsServiceException() {
    }

    public StatisticsServiceException(String message) {
        super(message);
    }

    public StatisticsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
