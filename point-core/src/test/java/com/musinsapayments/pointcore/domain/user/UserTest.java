package com.musinsapayments.pointcore.domain.user;

import com.musinsapayments.pointcore.exception.user.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.musinsapayments.pointcore.exception.user.UserErrorCode.INVALID_MAX_POINTS;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("최대 포인트를 변경할 수 있다.")
    void changeMaxPoints_1() {
        // given
        User user = new User("tester-A", 100);
        // when
        user.changeMaxPoints(200);
        // then
        assertEquals(200, user.getMaxPoints());
    }

    @Test
    @DisplayName("최대 포인트는 0 이상이어야 한다.")
    void changeMaxPoints_2() {
        // given
        User user = new User("tester-A", 100);
        // when & then
        UserException exception = assertThrows(UserException.class, () -> user.changeMaxPoints(-1));
        assertEquals(INVALID_MAX_POINTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("최대 포인트는 2,000,000,000 (20억) 이하여야 한다.")
    void changeMaxPoints_3() {
        // given
        User user = new User("tester-A", 100);
        // when & then
        UserException exception = assertThrows(UserException.class, () -> user.changeMaxPoints(2000000001));
        assertEquals(INVALID_MAX_POINTS, exception.getErrorCode());
    }

}