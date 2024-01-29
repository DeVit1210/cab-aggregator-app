package com.modsen.driver.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MultipleApiException {
    private List<String> messageList;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;

    public static MultipleApiException of(List<String> messageList, HttpStatus status) {
        return new MultipleApiException(messageList, status, LocalDateTime.now());
    }
}
