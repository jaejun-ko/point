package com.musinsapayments.pointcore.domain.point;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointConfigureServiceImpl implements PointConfigureService {

    private final PointStore pointStore;
    private final PointReader pointReader;

    @Transactional
    @Override
    public PointInfo.PointConfigureMain createPointConfigure(PointCommand.CreatePointConfigure command) {

        var activePointConfigure = pointReader.getActivePointConfigures();
        activePointConfigure.forEach(PointConfigure::deactivate);   // 이전 활성화 설정 비활성화

        var pointConfigure = command.toEntity();
        var storedPointConfigure = pointStore.store(pointConfigure);

        return new PointInfo.PointConfigureMain(storedPointConfigure);
    }

    @Transactional(readOnly = true)
    @Override
    public PointInfo.PointConfigureMain getActivePointConfigure() {

        var activePointConfigure = pointReader.getActivePointConfigure();
        return new PointInfo.PointConfigureMain(activePointConfigure);
    }
}
