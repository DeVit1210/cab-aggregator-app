package com.modsen.rating.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiExceptionInfo {
    private String message;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;

    public static ApiExceptionInfo of(String message, HttpStatus status) {
        return new ApiExceptionInfo(message, status, LocalDateTime.now());
    }
}
