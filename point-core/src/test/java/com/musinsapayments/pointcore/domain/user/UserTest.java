package com.musinsapayments.pointcore.domain.user;

import com.musinsapayments.pointcore.exception.user.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.musinsapayments.pointcore.exception.user.UserErrorCode.INVALID_MAX_POINTS;
import static com.musinsapayments.pointcore.exception.user.UserErrorCode.INVALID_NAME;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("사용자 이름과 최대 포인트를 설정할 수 있다.")
    void constructor_1() {
        // given
        User user = new User("tester-A", 100);
        // then
        assertEquals("tester-A", user.getName());
        assertEquals(100, user.getMaxPoints());
    }

    @Test
    @DisplayName("사용자 이름은 50자 이하여야 한다.")
    void constructor_2() {
        // when & then
        UserException exception = assertThrows(UserException.class, () -> new User("abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz", 100));
        assertEquals(INVALID_NAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자 이름은 null이거나 빈 문자열이면 안된다.")
    void constructor_3() {
        // when & then
        UserException exception = assertThrows(UserException.class, () -> new User(null, 100));
        assertEquals(INVALID_NAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자 이름은 null이거나 빈 문자열이면 안된다.")
    void constructor_4() {
        // when & then
        UserException exception = assertThrows(UserException.class, () -> new User("", 100));
        assertEquals(INVALID_NAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("최대 포인트는 0 이상이어야 한다.")
    void constructor_5() {
        // when & then
        UserException exception = assertThrows(UserException.class, () -> new User("tester-A", -1));
        assertEquals(INVALID_MAX_POINTS, exception.getErrorCode());
    }

    @Test
    @DisplayName("최대 포인트는 2,000,000,000 (20억) 이하여야 한다.")
    void constructor_6() {
        // when & then
        UserException exception = assertThrows(UserException.class, () -> new User("tester-A", 2000000001));
        assertEquals(INVALID_MAX_POINTS, exception.getErrorCode());
    }

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