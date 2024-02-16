package com.modsen.promocode.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.promocode.exception.ApiExceptionInfo;
import com.modsen.promocode.exception.base.BadRequestException;
import com.modsen.promocode.exception.base.ConflictException;
import com.modsen.promocode.exception.base.NotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;
    private final ErrorDecoder errorDecoder;

    public CustomErrorDecoder() {
        this.errorDecoder = new ErrorDecoder.Default();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Exception decode(String s, Response response) {
        try (InputStream bodyInputStream = response.body().asInputStream()) {
            ApiExceptionInfo exceptionInfo = objectMapper.readValue(bodyInputStream, ApiExceptionInfo.class);
            return switch (exceptionInfo.getHttpStatus()) {
                case NOT_FOUND -> new NotFoundException(exceptionInfo.getMessage());
                case CONFLICT -> new ConflictException(exceptionInfo.getMessage());
                case BAD_REQUEST -> new BadRequestException(exceptionInfo.getMessage());
                default -> errorDecoder.decode(s, response);
            };
        } catch (IOException e) {
            return errorDecoder.decode(s, response);
        }
    }
}
