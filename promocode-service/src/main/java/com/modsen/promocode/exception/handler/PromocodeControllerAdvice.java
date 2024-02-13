package com.modsen.promocode.exception.handler;

import com.modsen.promocode.exception.ApiExceptionInfo;
import com.modsen.promocode.exception.InvalidRideAmountForUsingPromocodeException;
import com.modsen.promocode.exception.MultipleApiExceptionInfo;
import com.modsen.promocode.exception.PromocodeAlreadyAppliedException;
import com.modsen.promocode.exception.PromocodeAlreadyExistsException;
import com.modsen.promocode.exception.PromocodeNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class PromocodeControllerAdvice {
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

    @ExceptionHandler({SQLIntegrityConstraintViolationException.class, InvalidRideAmountForUsingPromocodeException.class})
    public ResponseEntity<ApiExceptionInfo> handleBadRequestException(RuntimeException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({PromocodeAlreadyAppliedException.class, PromocodeAlreadyExistsException.class})
    public ResponseEntity<ApiExceptionInfo> handleAlreadyExistsException(RuntimeException e) {
        return generateApiExceptionResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PromocodeNotFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleNotFoundException(RuntimeException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiExceptionInfo> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}