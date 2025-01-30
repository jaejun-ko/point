package com.musinsapayments.pointcore.common.response;

import com.musinsapayments.pointcore.exception.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private Result result;
    private T data;
    private String message;
    private String errorCode;

    public static ApiResponse success() {

        return ApiResponse.builder()
                .result(Result.SUCCESS)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {

        return (ApiResponse<T>) ApiResponse.builder()
                .result(Result.SUCCESS)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return (ApiResponse<T>) ApiResponse.builder()
                .result(Result.SUCCESS)
                .data(data)
                .message(message)
                .build();
    }

    public static ApiResponse fail(String message, BaseErrorCode errorCode) {
        return ApiResponse.builder()
                .result(Result.FAIL)
                .message(message)
                .errorCode(errorCode.getCode())
                .build();
    }

    public static ApiResponse fail(BaseErrorCode errorCode) {
        return ApiResponse.builder()
                .result(Result.FAIL)
                .errorCode(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    public enum Result {
        SUCCESS, FAIL
    }
}
