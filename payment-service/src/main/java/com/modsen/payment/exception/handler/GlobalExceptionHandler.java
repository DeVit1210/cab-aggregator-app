package com.modsen.payment.exception.handler;

import com.modsen.payment.exception.AlreadyExistsException;
import com.modsen.payment.exception.ApiExceptionInfo;
import com.modsen.payment.exception.CardIsNotDefaultException;
import com.modsen.payment.exception.CustomStripeException;
import com.modsen.payment.exception.EntityNotFoundException;
import com.modsen.payment.exception.IncufficientAccountBalanceException;
import com.modsen.payment.exception.InvalidCreditCardHolderException;
import com.modsen.payment.exception.MultipleApiExceptionInfo;
import com.modsen.payment.exception.PageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
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

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ApiExceptionInfo> handleSqlIntegrityException(SQLIntegrityConstraintViolationException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PageException.class)
    public ResponseEntity<ApiExceptionInfo> handlePageException(PageException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncufficientAccountBalanceException.class)
    public ResponseEntity<ApiExceptionInfo> handleIncufficientBalanceException(IncufficientAccountBalanceException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidCreditCardHolderException.class)
    public ResponseEntity<ApiExceptionInfo> handleInvalidCreditCardHolderException(InvalidCreditCardHolderException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomStripeException.class)
    public ResponseEntity<ApiExceptionInfo> handleCustomStripeException(CustomStripeException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CardIsNotDefaultException.class)
    public ResponseEntity<ApiExceptionInfo> handleCardIsNotDefaultException(CardIsNotDefaultException e) {
        return generateApiExceptionResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiExceptionInfo> handleAlreadyExistsException(AlreadyExistsException e) {
        return generateApiExceptionResponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiExceptionInfo> handleEntityNotFoundException(EntityNotFoundException e) {
        return generateApiExceptionResponse(e, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ApiExceptionInfo> generateApiExceptionResponse(Throwable e, HttpStatus status) {
        ApiExceptionInfo apiException = ApiExceptionInfo.of(e.getMessage(), status);
        return new ResponseEntity<>(apiException, status);
    }
}
