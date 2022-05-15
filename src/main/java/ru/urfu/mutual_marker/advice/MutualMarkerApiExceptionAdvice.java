package ru.urfu.mutual_marker.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MutualMarkerApiExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> mutualMarkerApiExceptionHandler(Throwable ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
