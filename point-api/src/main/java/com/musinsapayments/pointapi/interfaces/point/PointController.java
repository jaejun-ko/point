package com.musinsapayments.pointapi.interfaces.point;

import com.musinsapayments.pointapi.application.point.PointFacade;
import com.musinsapayments.pointapi.interfaces.point.dto.PointDto;
import com.musinsapayments.pointcore.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/points")
public class PointController {

    private final PointFacade pointFacade;

    @PostMapping("/configures")
    public ApiResponse createPointConfigure(@Valid @RequestBody PointDto.CreatePointConfigureRequest request) {

        var command = request.toCommand();
        pointFacade.createPointConfigure(command);
        return ApiResponse.success();
    }

    @GetMapping("/configures")
    public ApiResponse getActivePointConfigure() {

        var pointConfigure = pointFacade.getActivePointConfigure();
        var response = PointDto.PointConfigureMain.from(pointConfigure);
        return ApiResponse.success(response);
    }

    @PostMapping("/add")
    public ApiResponse addPoint(@Valid @RequestBody PointDto.AddPointRequest request) {

        var command = request.toCommand();
        pointFacade.addPoint(command);
        return ApiResponse.success();
    }

    @PostMapping("/add/cancel")
    public ApiResponse addCancelPoint(@Valid @RequestBody PointDto.AddCancelPointRequest request) {

        var command = request.toCommand();
        pointFacade.addCancelPoint(command);
        return ApiResponse.success();
    }

    @PostMapping("/use")
    public ApiResponse usePoint(@Valid @RequestBody PointDto.UsePointRequest request) {

        var command = request.toCommand();
        pointFacade.usePoint(command);
        return ApiResponse.success();
    }

    @PostMapping("/use/cancel")
    public ApiResponse useCancelPoint(@Valid @RequestBody PointDto.UseCancelPointRequest request) {

        var command = request.toCommand();
        pointFacade.useCancelPoint(command);
        return ApiResponse.success();
    }

    // 원활한 테스트를 위해 특정 적립 포인트를 강제로 만료시킬 수 있는 API 를 제공합니다.
    @PostMapping("/expire-force")
    public ApiResponse expirePoint(@Valid @RequestBody PointDto.ExpirePointRequest request) {

        request.setForce(true);

        var command = request.toCommand();
        pointFacade.expirePoint(command);
        return ApiResponse.success();
    }

    @GetMapping("/{userId}")
    public ApiResponse getPointInfo(@PathVariable Long userId) {

        var pointInfo = pointFacade.getPointInfo(userId);
        var response = PointDto.PointMain.from(pointInfo);
        return ApiResponse.success(response);
    }
}
