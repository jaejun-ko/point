package com.musinsapayments.pointcore.exception.user;

import com.musinsapayments.pointcore.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {

    USER_NOT_EXIST("사용자가 존재하지 않습니다."),
    INVALID_NAME("사용자 이름이 잘못되었습니다."),
    INVALID_MAX_POINTS("최대 포인트 값 설정이 잘못되었습니다."),
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
