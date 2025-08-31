package com.ezlevup.jober.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Map<String, String> fieldErrors;
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }
    
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null, null);
    }
    
    public static <T> ApiResponse<T> failure(String message, Map<String, String> fieldErrors) {
        return new ApiResponse<>(false, message, null, fieldErrors);
    }
}