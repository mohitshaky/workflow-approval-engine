package com.mohit.workflow.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ApiResponse<T> {
    // Getters and setters
    private String message;
    private T data;
    private String status;
    private LocalDateTime timestamp;
    
    public ApiResponse(String message, T data) {
        this.message = message;
        this.data = data;
        this.status = "SUCCESS";
        this.timestamp = LocalDateTime.now();
    }
    
    public ApiResponse(String message, T data, String status) {
        this.message = message;
        this.data = data;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

}