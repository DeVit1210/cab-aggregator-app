package com.modsen.passenger.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.modsen.passenger.exception.BadRequestException;
import com.modsen.passenger.exception.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;
    private final ErrorDecoder errorDecoder;

    public CustomErrorDecoder() {
        this.errorDecoder = new Default();
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Exception decode(String s, Response response) {
        try (InputStream bodyInputStream = response.body().asInputStream()) {
            ErrorResponseMessage exceptionInfo = objectMapper.readValue(bodyInputStream, ErrorResponseMessage.class);
            return switch (exceptionInfo.getHttpStatus()) {
                case NOT_FOUND -> new NotFoundException(exceptionInfo.getMessage());
                case BAD_REQUEST -> new BadRequestException(exceptionInfo.getMessage());
                default -> errorDecoder.decode(s, response);
            };
        } catch (IOException e) {
            return errorDecoder.decode(s, response);
        }
    }

    @Data
    static class ErrorResponseMessage {
        String message;
        HttpStatus httpStatus;
        LocalDateTime timestamp;
    }
}

