package com.musinsapayments.pointcore.exception.user;

import com.musinsapayments.pointcore.exception.BaseErrorCode;
import com.musinsapayments.pointcore.exception.BaseException;

public class UserException extends BaseException {

    public UserException(BaseErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public UserException(BaseErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
