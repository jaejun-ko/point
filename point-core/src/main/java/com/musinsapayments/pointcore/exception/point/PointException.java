package com.musinsapayments.pointcore.exception.point;


import com.musinsapayments.pointcore.exception.BaseErrorCode;
import com.musinsapayments.pointcore.exception.BaseException;

public class PointException extends BaseException {

    public PointException(BaseErrorCode errorCode) {
        super(errorCode, errorCode.getMessage());
    }

    public PointException(BaseErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
