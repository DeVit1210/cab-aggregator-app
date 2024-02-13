package com.modsen.driver.exception.handler;

import com.modsen.driver.exception.ApiExceptionInfo;
import com.modsen.driver.exception.DriverAlreadyOnlineException;
import com.modsen.driver.exception.DriverNotAvailableException;
import com.modsen.driver.exception.DriverNotFoundException;
import com.modsen.driver.exception.MultipleApiExceptionInfo;
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
        MultipleApiExceptionInfo exception = MultipleApiExceptionInfo.of(exceptionMessageList, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(exception, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class, PageException.class})
    public ResponseEntity<ApiExceptionInfo> handleBadRequestException(RuntimeException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleDriverNotFoundException(DriverNotFoundException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({DriverAlreadyOnlineException.class, DriverNotAvailableException.class})
    public ResponseEntity<ApiExceptionInfo> handleDriverConflictException(RuntimeException e) {
        return generateApiExceptionResponse(e, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ApiExceptionInfo> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}
