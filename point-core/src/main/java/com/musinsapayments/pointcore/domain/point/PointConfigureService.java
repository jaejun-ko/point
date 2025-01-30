package com.musinsapayments.pointcore.domain.point;

public interface PointConfigureService {

    PointInfo.PointConfigureMain createPointConfigure(PointCommand.CreatePointConfigure createPointConfigure);
    PointInfo.PointConfigureMain getActivePointConfigure();
}
