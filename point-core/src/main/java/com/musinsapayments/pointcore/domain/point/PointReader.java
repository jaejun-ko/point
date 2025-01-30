package com.musinsapayments.pointcore.domain.point;

import java.util.List;

public interface PointReader {
    PointConfigure getActivePointConfigure();
    List<PointConfigure> getActivePointConfigures();

    Point getPoint(long pointId);
    List<Point> getRemainingPoints(long userId);
    int getTotalRemainingPoints(long userId);

    List<PointUsage> getCancellablePointUsages(long pointId);
}
