package com.musinsapayments.pointcore.exception.point;

import com.musinsapayments.pointcore.exception.BaseErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PointErrorCode implements BaseErrorCode {


    INVALID_USER("사용자 정보가 잘못되었습니다."),
    INVALID_POINT_AMOUNT("포인트 금액이 잘못되었습니다."),
    INVALID_POINT_TRANSACTION_TYPE("포인트 거래 유형이 잘못되었습니다."),
    INVALID_ORDER_ID("주문 ID가 잘못되었습니다."),
    EXCEED_MAX_AMOUNT_PER_ADD("1회 최대 적립 가능한 포인트를 초과하였습니다."),
    EXCEED_MAX_AMOUNT("사용자의 최대 보유 가능 포인트를 초과하였습니다."),
    ALREADY_USED_POINT("이미 사용된 포인트는 취소할 수 없습니다."),
    NOT_EXIST_ACTIVE_POINT_CONFIGURE("활성화된 포인트 설정 정보가 존재하지 않습니다."),
    POINT_NOT_EXIST("존재하지 않는 포인트 정보입니다."),
    POINT_NOT_ENOUGH("포인트가 부족합니다."),
    NOT_USED_POINT("사용되지 않은 포인트입니다."),
    EXCEED_MAX_CANCELLABLE_USE_AMOUNT("취소할 포인트가 사용한 포인트를 초과했습니다."),
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
