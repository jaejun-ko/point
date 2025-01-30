  package com.musinsapayments.pointcore.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BaseException extends RuntimeException {

    private final BaseErrorCode errorCode;
    private final String message;
}
