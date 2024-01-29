package com.modsen.driver.exception.handler;

import com.modsen.driver.exception.ApiException;
import com.modsen.driver.exception.DriverNotFoundException;
import com.modsen.driver.exception.MultipleApiException;
import com.modsen.driver.exception.PageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class DriverControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> exceptionMessageList = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        MultipleApiException exception = MultipleApiException.of(exceptionMessageList, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiException> handleSqlIntegrityException(SQLIntegrityConstraintViolationException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ApiException> handlePassengerNotFoundException(DriverNotFoundException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PageException.class)
    public ResponseEntity<ApiException> handlePageException(PageException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiException> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiException apiException = ApiException.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}
