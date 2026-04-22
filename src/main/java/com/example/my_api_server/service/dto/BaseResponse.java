package com.example.my_api_server.service.dto;

public record BaseResponse<T>(
        String message,
        T data,
        int at,
        double c
) {
}
