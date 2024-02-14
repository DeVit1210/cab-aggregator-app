package com.modsen.rating.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.rating.exception.ApiExceptionInfo;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;
    private final ErrorDecoder errorDecoder;

    public CustomErrorDecoder() {
        this.errorDecoder = new Default();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Exception decode(String s, Response response) {
        try (InputStream bodyInputStream = response.body().asInputStream()) {
            ApiExceptionInfo exceptionInfo = objectMapper.readValue(bodyInputStream, ApiExceptionInfo.class);
            return switch (exceptionInfo.getHttpStatus()) {
                default -> errorDecoder.decode(s, response);
            };
        } catch (IOException e) {
            return errorDecoder.decode(s, response);
        }
    }
}
