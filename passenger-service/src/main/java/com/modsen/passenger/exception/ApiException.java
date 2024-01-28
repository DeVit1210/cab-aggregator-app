package com.modsen.passenger.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiException {
    private String message;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;
}
