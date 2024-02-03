package com.modsen.rating.exception.handler;

import com.modsen.rating.exception.ApiExceptionInfo;
import com.modsen.rating.exception.MultipleApiExceptionInfo;
import com.modsen.rating.exception.PageException;
import com.modsen.rating.exception.RatingNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class RatingControllerAdvice {
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

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiExceptionInfo> handleSqlIntegrityException(SQLIntegrityConstraintViolationException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RatingNotFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handlePassengerNotFoundException(RatingNotFoundException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PageException.class)
    public ResponseEntity<ApiExceptionInfo> handlePageException(PageException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiExceptionInfo> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}
