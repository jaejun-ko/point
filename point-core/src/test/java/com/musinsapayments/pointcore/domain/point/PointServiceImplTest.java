package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.domain.user.User;
import com.musinsapayments.pointcore.domain.user.UserReader;
import com.musinsapayments.pointcore.exception.point.PointErrorCode;
import com.musinsapayments.pointcore.exception.point.PointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static com.musinsapayments.pointcore.exception.point.PointErrorCode.EXCEED_MAX_AMOUNT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.EXCEED_MAX_AMOUNT_PER_ADD;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.EXCEED_MAX_CANCELLABLE_USE_AMOUNT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.NOT_USED_POINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PointServiceImplTest {

    @InjectMocks
    private PointServiceImpl pointService;

    @Mock
    private UserReader userReader;

    @Mock
    private PointStore pointStore;

    @Mock
    private PointReader pointReader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("포인트를 적립한다.")
    void addPoints_1() {
        // given
        var command = new PointCommand.AddPoint(1, 1000, false, 30, 1);
        var user = new User("테스트 유저", 1000);
        var point = Point.builder()
                .user(user)
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        when(userReader.getUser(1L)).thenReturn(user);
        when(pointReader.getActivePointConfigure()).thenReturn(new PointConfigure(100000));
        pointService.addPoint(command);
        // then
        verify(pointStore, times(1)).store(any(Point.class));
    }

    @Test
    @DisplayName("적립하려는 포인트가 1회 최대 적립 가능 금액을 초과하면 예외가 발생한다.")
    void addPoints_2() {
        // given
        var command = new PointCommand.AddPoint(1, 100000, false, 30, 1);
        var user = new User("테스트 유저", 1000000);
        var pointConfigure = new PointConfigure(50000);

        // when
        when(userReader.getUser(1L)).thenReturn(user);
        when(pointReader.getActivePointConfigure()).thenReturn(pointConfigure);
        // then
        PointException exception = assertThrows(PointException.class, () -> pointService.addPoint(command));
        assertEquals(EXCEED_MAX_AMOUNT_PER_ADD, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자의 최대 보유 가능 포인트를 초과하면 예외가 발생한다.")
    void addPoints_3() {
        // given
        var command = new PointCommand.AddPoint(1, 1000, false, 30, 1);
        var user = new User("테스트 유저", 100);
        var pointConfigure = new PointConfigure(100000);
        // when
        when(userReader.getUser(1L)).thenReturn(user);
        when(pointReader.getActivePointConfigure()).thenReturn(pointConfigure);
        when(pointReader.getTotalRemainingPoints(1L)).thenReturn(100);
        // then
        PointException exception = assertThrows(PointException.class, () -> pointService.addPoint(command));
        assertEquals(EXCEED_MAX_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트를 적립을 취소한다.")
    void addCancelPoints_1() {
        // given
        var command = new PointCommand.AddCancelPoint(1);
        var point = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        when(pointReader.getPoint(1L)).thenReturn(point);
        pointService.addCancelPoint(command);
        // then
        verify(pointStore, times(1)).store(any(Point.class));
        assertEquals(0, point.getRemainingAmount());
    }

    @Test
    @DisplayName("이미 사용된 포인트를 적립 취소하려고 하면 예외가 발생한다.")
    void addCancelPoints_2() {
        // given
        var command = new PointCommand.AddCancelPoint(1);
        var point = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(1000)
                .remainingAmount(0)
                .transactionType(PointTransactionType.ADD)
                .build();
        // when
        when(pointReader.getPoint(1L)).thenReturn(point);
        // then
        PointException exception = assertThrows(PointException.class, () -> pointService.addCancelPoint(command));
        assertEquals(PointErrorCode.ALREADY_USED_POINT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트를 사용한다.")
    void usePoint_1() {

        var command = new PointCommand.UsePoint(1, 1000, 1);
        var user = new User("테스트 유저", 100000);
        var usePoint = command.toEntity(user);
        var remainingPoints = List.of(
                Point.builder()
                        .user(user)
                        .amount(500)
                        .remainingAmount(500)
                        .transactionType(PointTransactionType.ADD)
                        .build(),
                Point.builder()
                        .user(user)
                        .amount(500)
                        .remainingAmount(500)
                        .transactionType(PointTransactionType.ADD)
                        .build()
        );
        var pointUsage1 = new PointUsage(usePoint, remainingPoints.get(0), -500);
        var pointUsage2 = new PointUsage(usePoint, remainingPoints.get(1), -500);
        // when
        when(userReader.getUser(any(Long.class))).thenReturn(user);
        when(pointStore.store(any(Point.class))).thenReturn(usePoint);
        when(pointReader.getRemainingPoints(any(Long.class))).thenReturn(remainingPoints);

        pointService.usePoint(command);

        // then
        verify(pointStore, times(1)).store(any(Point.class));
        verify(pointStore, times(2)).store(any(PointUsage.class));
        assertEquals(0, remainingPoints.get(0).getRemainingAmount());
        assertEquals(0, remainingPoints.get(1).getRemainingAmount());
    }

    @Test
    @DisplayName("포인트를 사용할 때 차감되는 포인트 > 현재 잔여 포인트 이면 예외가 발생한다.")
    void usePoint_2() {
        // given
        var command = new PointCommand.UsePoint(1, 1500, 1);
        var user = new User("테스트 유저", 1000);
        var usePoint = command.toEntity(user);
        var remainingPoints = List.of(
                Point.builder()
                        .user(user)
                        .amount(1000)
                        .remainingAmount(1000)
                        .transactionType(PointTransactionType.ADD)
                        .build()
        );
        // when
        when(userReader.getUser(1L)).thenReturn(user);
        when(pointStore.store(any(Point.class))).thenReturn(usePoint);
        when(pointReader.getRemainingPoints(1L)).thenReturn(remainingPoints);
        // then
        PointException exception = assertThrows(PointException.class, () -> pointService.usePoint(command));
        assertEquals(PointErrorCode.POINT_NOT_ENOUGH, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용된 포인트를 취소한다.")
    void useCancelPoint_1() {
        // given
        var command = new PointCommand.UseCancelPoint(1L, 1L, 1000);
        var point = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(-1000)
                .remainingAmount(-1000)
                .transactionType(PointTransactionType.USE)
                .orderId(1)
                .build();
        var relatedPoints = List.of(
                Point.builder()
                        .user(new User("테스트 유저", 1000))
                        .amount(500)
                        .remainingAmount(0)
                        .transactionType(PointTransactionType.ADD)
                        .expireAt(LocalDate.now().plusDays(365))
                        .build(),
                Point.builder()
                        .user(new User("테스트 유저", 1000))
                        .amount(500)
                        .remainingAmount(0)
                        .transactionType(PointTransactionType.ADD)
                        .expireAt(LocalDate.now().plusDays(365))
                        .build()
        );
        var pointUsages = List.of(
                new PointUsage(point, relatedPoints.get(0), -500),
                new PointUsage(point, relatedPoints.get(1), -500)
        );
        var useCancelPoint = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.USE_CANCEL)
                .relatedPoint(point)
                .build();
        // when
        when(pointReader.getPoint(1L)).thenReturn(point);
        when(pointReader.getCancellablePointUsages(any(Long.class))).thenReturn(pointUsages);
        when(pointStore.store(any(Point.class))).thenReturn(useCancelPoint);

        pointService.useCancelPoint(command);
        // then
        verify(pointStore, times(1)).store(any(Point.class));
        verify(pointStore, times(2)).store(any(PointUsage.class));
        assertEquals(500, relatedPoints.get(0).getRemainingAmount());
        assertEquals(500, relatedPoints.get(1).getRemainingAmount());
        assertEquals(0, point.getRemainingAmount());
    }

    @Test
    @DisplayName("사용된 포인트를 취소할 때 이미 만료된 포인트가 있으면 새로운 포인트를 생성한다.")
    void useCancelPoint_2() {
        // given
        var command = new PointCommand.UseCancelPoint(1L, 1L, 1000);
        var point = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(-1000)
                .remainingAmount(-1000)
                .transactionType(PointTransactionType.USE)
                .orderId(1)
                .build();
        var relatedPoints = List.of(
                Point.builder()
                        .user(new User("테스트 유저", 1000))
                        .amount(500)
                        .remainingAmount(0)
                        .transactionType(PointTransactionType.ADD)
                        .expireAt(LocalDate.now().minusDays(1))
                        .build(),
                Point.builder()
                        .user(new User("테스트 유저", 1000))
                        .amount(500)
                        .remainingAmount(0)
                        .transactionType(PointTransactionType.ADD)
                        .expireAt(LocalDate.now().plusDays(1))
                        .build()
        );
        var pointUsages = List.of(
                new PointUsage(point, relatedPoints.get(0), -500),
                new PointUsage(point, relatedPoints.get(1), -500)
        );
        var useCancelPoint = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(1000)
                .remainingAmount(1000)
                .transactionType(PointTransactionType.USE_CANCEL)
                .relatedPoint(point)
                .build();
        var newAddPoint = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(500)
                .remainingAmount(500)
                .transactionType(PointTransactionType.ADD)
                .expireAt(LocalDate.now().plusDays(365))
                .relatedPoint(relatedPoints.get(0))
                .build();
        // when
        when(pointReader.getPoint(1L)).thenReturn(point);
        when(pointReader.getCancellablePointUsages(any(Long.class))).thenReturn(pointUsages);
        when(pointStore.store(any(Point.class))).thenReturn(useCancelPoint);
        when(pointStore.store(any(Point.class))).thenReturn(newAddPoint);

        pointService.useCancelPoint(command);
        // then
        verify(pointStore, times(2)).store(any(Point.class));
        verify(pointStore, times(2)).store(any(PointUsage.class));
        assertEquals(500, relatedPoints.get(0).getRemainingAmount());
        assertEquals(500, relatedPoints.get(1).getRemainingAmount());
        assertEquals(0, point.getRemainingAmount());
    }

    @Test
    @DisplayName("사용된 포인트가 없으면 예외가 발생한다.")
    void useCancelPoint_3() {
        // given
        var command = new PointCommand.UseCancelPoint(1L, 1L, 1000);
        var point = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(-1000)
                .remainingAmount(-1000)
                .transactionType(PointTransactionType.USE)
                .orderId(1)
                .build();
        // when
        when(pointReader.getPoint(1L)).thenReturn(point);
        when(pointReader.getCancellablePointUsages(any(Long.class))).thenReturn(List.of());
        // then
        PointException exception = assertThrows(PointException.class, () -> pointService.useCancelPoint(command));
        assertEquals(NOT_USED_POINT, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용된 포인트 내역이 없으면 예외가 발생한다.")
    void useCancelPoint_4() {
        // given
        var command = new PointCommand.UseCancelPoint(1L, 1L, 1000);
        var point = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(-1000)
                .remainingAmount(-1000)
                .transactionType(PointTransactionType.USE)
                .orderId(1)
                .build();
        // when
        when(pointReader.getPoint(1L)).thenReturn(point);
        when(pointReader.getCancellablePointUsages(any(Long.class))).thenReturn(List.of());
        // then
        PointException exception = assertThrows(PointException.class, () -> pointService.useCancelPoint(command));
        assertEquals(NOT_USED_POINT, exception.getErrorCode());
    }

    @Test
    @DisplayName("취소할 수 있는 포인트가 부족하면 예외가 발생한다.")
    void useCancelPoint_5() {
        // given
        var command = new PointCommand.UseCancelPoint(1L, 1L, 1000);
        var point = Point.builder()
                .user(new User("테스트 유저", 1000))
                .amount(-1000)
                .remainingAmount(0)
                .transactionType(PointTransactionType.USE)
                .orderId(1)
                .build();
        // when
        when(pointReader.getPoint(1L)).thenReturn(point);
        // then
        PointException exception = assertThrows(PointException.class, () -> pointService.useCancelPoint(command));
        assertEquals(EXCEED_MAX_CANCELLABLE_USE_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트 정보를 조회한다.")
    void getPointInfo_1() {
        // given
        var user = new User("테스트 유저", 1000);
        var totalRemainingPoints = 1000;
        // when
        when(userReader.getUser(1L)).thenReturn(user);
        when(pointReader.getTotalRemainingPoints(1L)).thenReturn(totalRemainingPoints);
        // then
        var result = pointService.getPointInfo(1L);
        assertEquals(1000, result.getTotalRemainingPoints());
    }
}