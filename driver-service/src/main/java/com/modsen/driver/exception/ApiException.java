package com.modsen.driver.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiException {
    private String message;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;

    public static ApiException of(String message, HttpStatus status) {
        return new ApiException(message, status, LocalDateTime.now());
    }
}
