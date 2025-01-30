package com.musinsapayments.pointapi.application.point;

import com.musinsapayments.pointcore.domain.point.PointCommand;
import com.musinsapayments.pointcore.domain.point.PointConfigureService;
import com.musinsapayments.pointcore.domain.point.PointInfo;
import com.musinsapayments.pointcore.domain.point.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PointFacade {

    private final PointConfigureService pointConfigureService;
    private final PointService pointService;

    public void createPointConfigure(PointCommand.CreatePointConfigure command) {
        pointConfigureService.createPointConfigure(command);
    }

    public PointInfo.PointConfigureMain getActivePointConfigure() {
        return pointConfigureService.getActivePointConfigure();
    }

    public void addPoint(PointCommand.AddPoint command) {
        pointService.addPoint(command);
    }

    public void addCancelPoint(PointCommand.AddCancelPoint command) {
        pointService.addCancelPoint(command);
    }

    public void usePoint(PointCommand.UsePoint command) {
        pointService.usePoint(command);
    }

    public void useCancelPoint(PointCommand.UseCancelPoint command) {
        pointService.useCancelPoint(command);
    }

    public void expirePoint(PointCommand.ExpirePoint command) {
        pointService.expirePoint(command);
    }

    public PointInfo.PointMain getPointInfo(Long userId) {
        return pointService.getPointInfo(userId);
    }
}
