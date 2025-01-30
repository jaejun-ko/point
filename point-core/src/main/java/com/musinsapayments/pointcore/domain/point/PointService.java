package com.musinsapayments.pointcore.domain.point;

public interface PointService {

    void addPoint(PointCommand.AddPoint command);
    void addCancelPoint(PointCommand.AddCancelPoint command);
    void usePoint(PointCommand.UsePoint command);
    void useCancelPoint(PointCommand.UseCancelPoint command);
    void expirePoint(PointCommand.ExpirePoint command);

    PointInfo.PointMain getPointInfo(Long userId);
}
