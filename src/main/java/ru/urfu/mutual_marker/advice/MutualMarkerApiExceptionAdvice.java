package ru.urfu.mutual_marker.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class MutualMarkerApiExceptionAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> mutualMarkerApiExceptionHandler(Throwable ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
