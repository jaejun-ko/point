package com.musinsapayments.pointcore.infrastructure.point;

import com.musinsapayments.pointcore.domain.point.Point;
import com.musinsapayments.pointcore.domain.point.PointConfigure;
import com.musinsapayments.pointcore.domain.point.PointReader;
import com.musinsapayments.pointcore.domain.point.PointUsage;
import com.musinsapayments.pointcore.exception.point.PointException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.musinsapayments.pointcore.configuration.CacheConfiguration.POINT_CACHE;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.NOT_EXIST_ACTIVE_POINT_CONFIGURE;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.POINT_NOT_EXIST;

@RequiredArgsConstructor
@Component
public class PointReaderImpl implements PointReader {

    private final PointConfigureRepository pointConfigureRepository;
    private final PointRepository pointRepository;
    private final PointUsageRepository pointUsageRepository;

    @Cacheable(cacheNames = POINT_CACHE, key ="'active:configure'")
    @Override
    public PointConfigure getActivePointConfigure() {
        return pointConfigureRepository.findFirstByActiveIsTrue()
                .orElseThrow(() -> new PointException(NOT_EXIST_ACTIVE_POINT_CONFIGURE, "활성화된 포인트 설정 정보가 존재하지 않습니다."));
    }

    @Override
    public List<PointConfigure> getActivePointConfigures() {
        return pointConfigureRepository.findAllByActiveIsTrue();
    }

    @Override
    public Point getPoint(long id) {
        return pointRepository.findById(id)
                .orElseThrow(() -> new PointException(POINT_NOT_EXIST, "존재하지 않는 포인트 정보입니다. id: %d".formatted(id)));
    }

    @Override
    public List<Point> getRemainingPoints(long userId) {
        return pointRepository.findRemainingPoints(userId);
    }

    @Cacheable(cacheNames = POINT_CACHE, key = "'total:remaining:points:' + #userId")
    @Override
    public int getTotalRemainingPoints(long userId) {
        return getRemainingPoints(userId).stream()
                .mapToInt(Point::getRemainingAmount)
                .sum();
    }

    @Override
    public List<PointUsage> getCancellablePointUsages(long pointId) {
        return pointUsageRepository.findCancellablePointUsages(pointId);
    }
}
