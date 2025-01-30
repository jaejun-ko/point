package com.musinsapayments.pointcore.infrastructure.point;

import com.musinsapayments.pointcore.domain.point.Point;
import com.musinsapayments.pointcore.domain.point.PointConfigure;
import com.musinsapayments.pointcore.domain.point.PointStore;
import com.musinsapayments.pointcore.domain.point.PointUsage;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import static com.musinsapayments.pointcore.configuration.CacheConfiguration.POINT_CACHE;

@RequiredArgsConstructor
@Component
public class PointStoreImpl implements PointStore {

    private final PointConfigureRepository pointConfigureRepository;
    private final PointRepository pointRepository;
    private final PointUsageRepository pointUsageRepository;

    @CachePut(cacheNames = POINT_CACHE, key ="'active:configure'")
    @Override
    public PointConfigure store(PointConfigure pointConfigure) {
        return pointConfigureRepository.save(pointConfigure);
    }

    @CacheEvict(cacheNames = POINT_CACHE, key =  "'total:remaining:points:' + #point.user.id")
    @Override
    public Point store(Point point) {
        return pointRepository.save(point);
    }

    @Override
    public PointUsage store(PointUsage pointUsage) {
        return pointUsageRepository.save(pointUsage);
    }
}
