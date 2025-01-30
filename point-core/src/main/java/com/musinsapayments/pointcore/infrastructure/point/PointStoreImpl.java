package com.musinsapayments.pointcore.infrastructure.point;

import com.musinsapayments.pointcore.domain.point.Point;
import com.musinsapayments.pointcore.domain.point.PointConfigure;
import com.musinsapayments.pointcore.domain.point.PointStore;
import com.musinsapayments.pointcore.domain.point.PointUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PointStoreImpl implements PointStore {

    private final PointConfigureRepository pointConfigureRepository;
    private final PointRepository pointRepository;
    private final PointUsageRepository pointUsageRepository;

    @Override
    public PointConfigure store(PointConfigure pointConfigure) {
        return pointConfigureRepository.save(pointConfigure);
    }

    @Override
    public Point store(Point point) {
        return pointRepository.save(point);
    }

    @Override
    public PointUsage store(PointUsage pointUsage) {
        return pointUsageRepository.save(pointUsage);
    }
}
