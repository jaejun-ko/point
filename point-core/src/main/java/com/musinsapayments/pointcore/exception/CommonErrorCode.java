package com.musinsapayments.pointcore.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CommonErrorCode implements BaseErrorCode {

    COMMON_SYSTEM_ERROR("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    ;

    private final String message;

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
