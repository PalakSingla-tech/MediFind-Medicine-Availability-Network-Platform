package com.medifind.auth_service.exception;

import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.time.LocalDateTime;

@Data
public class ApiError {
    private LocalDateTime timeStamp;
    private String error;
    private HttpStatusCode statusCode;

    public ApiError(@Nullable String s) {this.timeStamp = LocalDateTime.now();}

    public ApiError(String error, String username, HttpStatusCode statusCode)
    {
        this("Username not foung with username " + username + HttpStatus.NOT_FOUND);
        this.error = error;
        this.statusCode = statusCode;
    }
}