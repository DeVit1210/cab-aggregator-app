package com.modsen.driver.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MultipleApiExceptionInfo {
    private List<String> messageList;
    private HttpStatus httpStatus;
    private LocalDateTime timestamp;

    public static MultipleApiExceptionInfo of(List<String> messageList, HttpStatus status) {
        return new MultipleApiExceptionInfo(messageList, status, LocalDateTime.now());
    }
}
