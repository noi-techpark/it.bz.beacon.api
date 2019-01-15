package it.bz.beacon.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({InvalidJwtAuthenticationException.class})
    protected ResponseEntity<Object> handleAuthException(InvalidJwtAuthenticationException ex, WebRequest request) {
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }
}
