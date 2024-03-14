package com.modsen.driver.exception.handler;

import com.modsen.driver.exception.ApiExceptionInfo;
import com.modsen.driver.exception.MultipleApiExceptionInfo;
import com.modsen.driver.exception.PageException;
import com.modsen.driver.exception.base.BadRequestException;
import com.modsen.driver.exception.base.ConflictException;
import com.modsen.driver.exception.base.NotFoundException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
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

    @ExceptionHandler({
            SQLIntegrityConstraintViolationException.class,
            PageException.class,
            BadRequestException.class
    })
    public ResponseEntity<ApiExceptionInfo> handleBadRequestException(RuntimeException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleNotFoundException(NotFoundException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiExceptionInfo> handleDriverConflictException(ConflictException e) {
        return generateApiExceptionResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<ApiExceptionInfo> handleServiceUnavailableException(FeignException.ServiceUnavailable e) {
        String exceptionMessage = "Some external service is unavailable!";
        return generateServiceUnavailableResponse(exceptionMessage);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<ApiExceptionInfo> handleCallNotPermittedException(CallNotPermittedException e) {
        String unavailableServiceName = e.getCausingCircuitBreakerName().split("-")[0];
        String exceptionMessage = String.format("calls to %s-service are forbidden for now!", unavailableServiceName);
        return generateServiceUnavailableResponse(exceptionMessage);
    }

    private ResponseEntity<ApiExceptionInfo> generateServiceUnavailableResponse(String exceptionMessage) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(exceptionMessage, HttpStatus.SERVICE_UNAVAILABLE);
        return new ResponseEntity<>(apiException, HttpStatus.SERVICE_UNAVAILABLE);
    }

    private ResponseEntity<ApiExceptionInfo> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}
