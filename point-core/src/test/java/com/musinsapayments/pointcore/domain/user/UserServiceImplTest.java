package com.musinsapayments.pointcore.domain.user;

import com.musinsapayments.pointcore.exception.user.UserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.musinsapayments.pointcore.exception.user.UserErrorCode.USER_NOT_EXIST;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserReader userReader;

    @Mock
    private UserStore userStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("유저를 등록한다.")
    void registerUser_1() {
        // given
        var command = new UserCommand.RegisterUser("테스트 유저", 1000);
        var user = new User("테스트 유저", 1000);
        // when
        when(userStore.store(ArgumentMatchers.any(User.class))).thenReturn(user);
        var result = userService.registerUser(command);
        // then
        assertEquals("테스트 유저", result.getName());
        assertEquals(1000, result.getMaxPoints());
    }

    @Test
    @DisplayName("유저의 최대 포인트를 변경한다.")
    void changeMaxPoints_1() {
        // given
        var command = new UserCommand.ChangeMaxPoints(1L, 2000);
        var user = new User("테스트 유저", 1000);
        // when
        when(userReader.getUser(1L)).thenReturn(user);
        when(userStore.store(ArgumentMatchers.any(User.class))).thenReturn(user);
        var result = userService.changeMaxPoints(command);
        // then
        assertEquals("테스트 유저", result.getName());
        assertEquals(2000, result.getMaxPoints());
    }

    @Test
    @DisplayName("존재하지 않는 유저의 최대 포인트를 변경하려고 하면 예외를 반환한다.")
    void changeMaxPoints_2() {
        // given
        var command = new UserCommand.ChangeMaxPoints(1L, 2000);
        // when
        when(userReader.getUser(1L)).thenThrow(new UserException(USER_NOT_EXIST));
        // then
        UserException exception = assertThrows(UserException.class, () -> userService.changeMaxPoints(command));
        assertEquals(USER_NOT_EXIST, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저를 조회한다.")
    void getUser_1() {
        // given
        var user = new User("테스트 유저", 1000);
        // when
        when(userReader.getUser(1L)).thenReturn(user);
        var result = userService.getUser(1L);
        // then
        assertEquals("테스트 유저", result.getName());
        assertEquals(1000, result.getMaxPoints());
    }

    @Test
    @DisplayName("존재하지 않는 유저를 조회하려고 하면 예외를 반환한다.")
    void getUser_2() {
        // given
        // when
        when(userReader.getUser(1L)).thenThrow(new UserException(USER_NOT_EXIST));
        // then
        UserException exception = assertThrows(UserException.class, () -> userService.getUser(1L));
        assertEquals(USER_NOT_EXIST, exception.getErrorCode());
    }

}