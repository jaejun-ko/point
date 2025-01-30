package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.exception.point.PointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_POINT_AMOUNT;
import static org.junit.jupiter.api.Assertions.*;

class PointConfigureTest {

    @Test
    @DisplayName("포인트 설정을 생성할 수 있다.")
    void changeMaxAmountPerAdd_1() {
        // given
        int maxAmountPerAdd = 1000;

        // when
        PointConfigure pointConfigure = new PointConfigure(maxAmountPerAdd);

        // then
        assertEquals(maxAmountPerAdd, pointConfigure.getMaxAmountPerAdd());
    }

    @Test
    @DisplayName("포인트 설정 생성 시 단일 적립 건 당 최대 포인트가 1 미만이면 예외가 발생한다.")
    void changeMaxAmountPerAdd_2() {
        // given
        int maxAmountPerAdd = 0;
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> new PointConfigure(maxAmountPerAdd));
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트 설정 생성 시 단일 적립 건 당 최대 포인트가 100000 초과이면 예외가 발생한다.")
    void changeMaxAmountPerAdd_3() {
        // given
        int maxAmountPerAdd = 100001;
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> new PointConfigure(maxAmountPerAdd));
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("단일 적립 건 당 최대 포인트 변경할 수 있다.")
    void changeMaxAmountPerAdd_4() {
        // given
        int maxAmountPerAdd = 1000;
        PointConfigure pointConfigure = new PointConfigure(maxAmountPerAdd);

        // when
        int newMaxAmountPerAdd = 100000;
        pointConfigure.changeMaxAmountPerAdd(newMaxAmountPerAdd);

        // then
        assertEquals(newMaxAmountPerAdd, pointConfigure.getMaxAmountPerAdd());
    }

    @Test
    @DisplayName("단일 적립 건 당 최대 포인트 변경 시 최대 포인트가 1 미만이면 예외가 발생한다.")
    void changeMaxAmountPerAdd_5() {
        // given
        int maxAmountPerAdd = 1000;
        PointConfigure pointConfigure = new PointConfigure(maxAmountPerAdd);
        // when
        int newMaxAmountPerAdd = 0;
        // then
        PointException exception = assertThrows(PointException.class, () -> pointConfigure.changeMaxAmountPerAdd(newMaxAmountPerAdd));
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("단일 적립 건 당 최대 포인트 변경 시 최대 포인트가 100000 초과이면 예외가 발생한다.")
    void changeMaxAmountPerAdd_6() {
        // given
        int maxAmountPerAdd = 1000;
        PointConfigure pointConfigure = new PointConfigure(maxAmountPerAdd);
        // when
        int newMaxAmountPerAdd = 100001;
        // then
        PointException exception = assertThrows(PointException.class, () -> pointConfigure.changeMaxAmountPerAdd(newMaxAmountPerAdd));
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트 설정을 활성화할 수 있다.")
    void activate_1() {
        // given
        PointConfigure pointConfigure = new PointConfigure(1000);
        // when
        pointConfigure.activate();
        // then
        assertTrue(pointConfigure.isActive());
    }

    @Test
    @DisplayName("포인트 설정을 비활성화할 수 있다.")
    void deactivate_1() {
        // given
        PointConfigure pointConfigure = new PointConfigure(1000);
        pointConfigure.activate();
        // when
        pointConfigure.deactivate();
        // then
        assertFalse(pointConfigure.isActive());
    }
}