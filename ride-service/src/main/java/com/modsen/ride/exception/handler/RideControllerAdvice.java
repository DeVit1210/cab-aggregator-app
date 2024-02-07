package com.modsen.ride.exception.handler;

import com.modsen.ride.exception.ApiExceptionInfo;
import com.modsen.ride.exception.IllegalRideStatusException;
import com.modsen.ride.exception.MultipleApiExceptionInfo;
import com.modsen.ride.exception.NoAvailableRideForDriver;
import com.modsen.ride.exception.PageException;
import com.modsen.ride.exception.RideNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class RideControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MultipleApiExceptionInfo> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
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
            NoAvailableRideForDriver.class,
            NoAvailableRideForDriver.class,
            IllegalRideStatusException.class
    })
    public ResponseEntity<ApiExceptionInfo> handleBadRequestException(Exception e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RideNotFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleRideNotFoundException(RideNotFoundException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiExceptionInfo> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}