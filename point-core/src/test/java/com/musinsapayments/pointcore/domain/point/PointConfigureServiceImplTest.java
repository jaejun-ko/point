package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.exception.point.PointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_POINT_AMOUNT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PointConfigureServiceImplTest {

    @InjectMocks
    private PointConfigureServiceImpl pointConfigureService;

    @Mock
    private PointStore pointStore;

    @Mock
    private PointReader pointReader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("포인트 설정을 생성한다.")
    void createPointConfigure_1() {
        // given
        var command = new PointCommand.CreatePointConfigure(100000);
        var pointConfigure = new PointConfigure(100000);
        // when
        when(pointStore.store(ArgumentMatchers.any(PointConfigure.class))).thenReturn(pointConfigure);
        var result = pointConfigureService.createPointConfigure(command);
        // then
        verify(pointReader, times(1)).getActivePointConfigures();
        assertEquals(100000, result.getMaxAmountPerAdd());
    }

    @Test
    @DisplayName("포인트 설정 생성 시 maxAmountPerAdd 가 1보다 작으면 예외가 발생한다.")
    void createPointConfigure_2() {
        // given
        var command = new PointCommand.CreatePointConfigure(0);
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> pointConfigureService.createPointConfigure(command));
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("포인트 설정 생성 시 maxAmountPerAdd 가 100000보다 크면 예외가 발생한다.")
    void createPointConfigure_3() {
        // given
        var command = new PointCommand.CreatePointConfigure(100001);
        // when
        // then
        PointException exception = assertThrows(PointException.class, () -> pointConfigureService.createPointConfigure(command));
        assertEquals(INVALID_POINT_AMOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("활성화된 포인트 설정을 조회한다.")
    void getActivePointConfigure_1() {
        // given
        var pointConfigure = new PointConfigure(100000);
        // when
        when(pointReader.getActivePointConfigure()).thenReturn(pointConfigure);
        var result = pointConfigureService.getActivePointConfigure();
        // then
        assertNotNull(result);
    }
}