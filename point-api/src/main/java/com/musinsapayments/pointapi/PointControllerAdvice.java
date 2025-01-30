package com.musinsapayments.pointapi;

import com.musinsapayments.pointcore.common.response.ApiResponse;
import com.musinsapayments.pointcore.exception.BaseErrorCode;
import com.musinsapayments.pointcore.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

import static com.musinsapayments.pointcore.exception.CommonErrorCode.COMMON_SYSTEM_ERROR;

@Slf4j
@RestControllerAdvice
public class PointControllerAdvice {

    private static final List<BaseErrorCode> SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST = List.of(
    );

    /**
     * http status: 500
     * result     : FAIL
     * 시스템 예외 상황이며, 집중 모니터링 대상이다.
     *
     * @param e
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public ApiResponse<?> handleException(Exception e) {

        log.error("server error ", e);

        return ApiResponse.fail(COMMON_SYSTEM_ERROR);
    }

    /**
     * http status: 200
     * result     : FAIL
     * 비즈니스 로직 중 발생한 오류를 처리한다.
     *
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BaseException.class)
    public ApiResponse<?> handleBaseException(BaseException e) {

        if (SPECIFIC_ALERT_TARGET_ERROR_CODE_LIST.contains(e.getErrorCode())) {
            log.error("[BaseException] cause = {}, errorMsg = {}",
                    NestedExceptionUtils.getMostSpecificCause(e),
                    NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        } else {
            log.info("[BaseException] cause = {}, errorMsg = {}",
                    NestedExceptionUtils.getMostSpecificCause(e),
                    NestedExceptionUtils.getMostSpecificCause(e).getMessage());
        }

        return ApiResponse.fail(e.getMessage(), e.getErrorCode());
    }
}
