package com.modsen.rating.exception.handler;

import com.modsen.rating.exception.ApiExceptionInfo;
import com.modsen.rating.exception.IllegalRatingAttemptException;
import com.modsen.rating.exception.MultipleApiExceptionInfo;
import com.modsen.rating.exception.PageException;
import com.modsen.rating.exception.RatingAlreadyExistsException;
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

    @ExceptionHandler({
            SQLIntegrityConstraintViolationException.class,
            PageException.class,
            IllegalRatingAttemptException.class
    })
    public ResponseEntity<ApiExceptionInfo> handleBadRequestException(Exception e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RatingNotFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handlePassengerNotFoundException(RatingNotFoundException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RatingAlreadyExistsException.class)
    public ResponseEntity<ApiExceptionInfo> handleRatingAlreadyExistsException(RatingAlreadyExistsException e) {
        return generateApiExceptionResponse(e, HttpStatus.CONFLICT);
    }

    private ResponseEntity<ApiExceptionInfo> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}
