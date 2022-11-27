package com.auth.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
@RestControllerAdvice
public class HandleException {

    private final Logger LOGGER = LoggerFactory.getLogger(HandleException.class);
//    @ExceptionHandler(Exception.class)
//    public final ResponseEntity<?> handleAllExceptions(Exception ex) {
//        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        LOGGER.info("handleUserNotFoundException");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ArgumentException.class)
    public final ResponseEntity<?> handleArgumentException(ArgumentException ex) {
        LOGGER.info("handleArgumentException");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(GroupNotFoundException.class)
    public final ResponseEntity<?> handleGroupNotFoundException(GroupNotFoundException ex) {
        LOGGER.info("handleGroupNotFoundException");
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
