package net.shyshkin.study.microservices.reactiveelasticqueryservice.api.error.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ElasticQueryServiceErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handle(AccessDeniedException e) {
        log.error("Access denied", e);
        return "You are not authorized to access this resource";
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(IllegalArgumentException e) {
        log.error("Illegal argument", e);
        return "Illegal argument: " + e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(MethodArgumentNotValidException e) {
        log.error("Method argument validation exception", e);
        return e.getAllErrors()
                .stream()
                .collect(Collectors.toMap(err -> ((FieldError) err).getField(), err -> err.getDefaultMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(WebExchangeBindException e) {
        log.error("Method argument validation exception", e);
        return e.getAllErrors()
                .stream()
                .collect(Collectors.toMap(err -> ((FieldError) err).getField(), err -> err.getDefaultMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handle(RuntimeException e) {
        log.error("Service runtime exception!", e);
        return "Service runtime exception: " + e.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handle(Exception e) {
        log.error("Internal server error!", e);
        return "A server error occurred!";
    }


}
