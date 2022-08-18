package com.handler;

import com.exception.APIError;
import com.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.function.Function;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final static String ENTITY_WITH_ID_NOT_FOUND = "Entity with the following id not found: ";

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> entityNotFoundErrorHandler(EntityNotFoundException entityNotFoundException, WebRequest webRequest) {

        Function<String, String> entityIdNotFoundFormatter =
                (message) -> ENTITY_WITH_ID_NOT_FOUND + entityNotFoundException.getEntityNotFoundId();

        APIError apiError = initializeAPIError(entityNotFoundException, entityIdNotFoundFormatter, webRequest);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> unknownExceptionHandler(Exception exception, WebRequest webRequest) {
        APIError apiError = initializeAPIError(exception, (message) -> message, webRequest);
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private APIError initializeAPIError(Exception exception, Function<String, String> messageFormatter, WebRequest webRequest) {
        APIError apiError = new APIError();
        apiError.setLocalDateTime(LocalDateTime.now());
        apiError.setUrlAccessed(webRequest.getDescription(false));
        apiError.setMessage(messageFormatter.apply(exception.getMessage()));
        apiError.setClassName(exception.getClass().getName());
        apiError.setMethodName(exception.getStackTrace()[0].getMethodName());
        return apiError;
    }
}
