package com.musinsapayments.pointcore.domain.point;

public interface PointStore {

    PointConfigure store(PointConfigure pointConfigure);

    Point store(Point point);

    PointUsage store(PointUsage pointUsage);
}
