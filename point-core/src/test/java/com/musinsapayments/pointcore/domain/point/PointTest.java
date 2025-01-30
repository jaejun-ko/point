package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.domain.user.User;
import com.musinsapayments.pointcore.exception.point.PointErrorCode;
import com.musinsapayments.pointcore.exception.point.PointException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_ORDER_ID;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_POINT_AMOUNT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_POINT_TRANSACTION_TYPE;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_USER;
import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    @DisplayName("포인트를 생성(적립)할 수 있다.")
    void constructor_1() {
        // given
        User user = new User("테스트 유저", 10000000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        // then
        assertNotNull(point);
    }

    @Test
    @DisplayName("포인트를 적립할 때 사용자가 null 이라면 예외가 발생한다.")
    void constructor_2() {
        // given
        User user = null;
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build());
        assertEquals(INVALID_USER, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트를 적립할 때 금액이 0보다 작다면 예외가 발생한다.")
    void constructor_3() {
        // given
        User user = new User("테스트 유저", 10000000);
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> Point.builder()
                .user(user)
                .amount(-1000)
                .remainingAmount(-1000)
                .transactionType(PointTransactionType.ADD)
                .build());
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트를 적립할 때 금액이 100000보다 크다면 예외가 발생한다.")
    void constructor_4() {
        // given
        User user = new User("테스트 유저", 10000000);
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> Point.builder()
                .user(user)
                .amount(100001)
                .remainingAmount(100001)
                .transactionType(PointTransactionType.ADD)
                .build());
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트 거래 유형이 null 이면 예외가 발생한다.")
    void constructor_5() {
        // given
        var user = new User("테스트 유저", 1000);
        // when & then
        PointException exception = assertThrows(PointException.class, () -> Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(null)
                .build());
        assertEquals(INVALID_POINT_TRANSACTION_TYPE, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트를 생성(사용)할 수 있다.")
    void constructor_6() {
        // given
        User user = new User("테스트 유저", 10000000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.USE)
                .orderId(1)
                .build();
        // when
        // then
        assertNotNull(point);
    }

    @Test
    @DisplayName("포인트를 사용할 때 주문 ID가 1보다 작다면 예외가 발생한다.")
    void constructor_7() {
        // given
        User user = new User("테스트 유저", 10000000);
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.USE)
                .orderId(0)
                .build());
        assertEquals(INVALID_ORDER_ID, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트 적립을 취소할 때 포인트가 이미 사용되었다면 true 를 반환한다.")
    void isAlreadyUsed_1() {
        // given
        User user = new User("테스트 유저", 1000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(500)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        boolean result = point.isAlreadyUsed();
        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("포인트가 사용되지 않았다면 false 를 반환한다.")
    void isAlreadyUsed_2() {
        // given
        User user = new User("테스트 유저", 1000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        boolean result = point.isAlreadyUsed();
        // then
        assertFalse(result);
    }

    @Test
    @DisplayName("포인트를 취소하면 남은 포인트가 0이 된다.")
    void cancel() {
        // given
        User user = new User("테스트 유저", 1000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        point.cancel();
        // then
        assertEquals(0, point.getRemainingAmount());
    }

    @Test
    @DisplayName("포인트를 사용하면 남은 포인트가 차감된다.")
    void use_1() {
        // given
        User user = new User("테스트 유저", 1000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        point.use(500);
        // then
        assertEquals(500, point.getRemainingAmount());
    }

    @Test
    @DisplayName("포인트를 사용할 때 차감되는 포인트 > 현재 잔여 포인트 이면 예외가 발생한다.")
    void use_2() {
        // given
        User user = new User("테스트 유저", 1000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when & then
        PointException exception = assertThrows(PointException.class, () -> point.use(1500));
        assertEquals(PointErrorCode.INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트가 만료되었다면 true 를 반환한다.")
    void isExpired() {
        // given
        User user = new User("테스트 유저", 1000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .expireAt(LocalDate.now().minusDays(1))
                .build();
        // when
        boolean result = point.isExpired();
        // then
        assertTrue(result);
    }

    @Test
    @DisplayName("포인트가 만료되지 않았다면 false 를 반환한다.")
    void isExpired_2() {
        // given
        User user = new User("테스트 유저", 1000);
        Point point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .expireAt(LocalDate.now().plusDays(1))
                .build();
        // when
        boolean result = point.isExpired();
        // then
        assertFalse(result);
    }
}