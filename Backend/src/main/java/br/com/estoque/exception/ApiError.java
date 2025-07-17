package br.com.estoque.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ApiError {
    private int status;
    private String message;
    private long timestamp;
    private Object details;

    public ApiError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public ApiError(int status, String message, Object details) {
        this(status, message);
        this.details = details;
    }
}

