package com.musinsapayments.pointapi.interfaces.point.dto;

import com.musinsapayments.pointcore.domain.point.PointCommand;
import com.musinsapayments.pointcore.domain.point.PointInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class PointDto {

    @Getter
    @Setter
    public static class CreatePointConfigureRequest {
        @Min(1) @Max(100000)
        private int maxAmountPerAdd;

        public PointCommand.CreatePointConfigure toCommand() {
            return new PointCommand.CreatePointConfigure(maxAmountPerAdd);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class PointConfigureMain {
        private final long id;
        private final int maxAmountPerAdd;

        public static PointConfigureMain from(PointInfo.PointConfigureMain pointConfigure) {
            return new PointConfigureMain(pointConfigure.getId(), pointConfigure.getMaxAmountPerAdd());
        }
    }

    @Getter
    @Setter
    public static class AddPointRequest {
        @Min(1) @Max(Long.MAX_VALUE)
        private long userId;
        @Min(1) @Max(100000)
        private int amount;
        private boolean manual;
        @Min(1) @Max(5 * 365)
        private int expireAfterDays = 365;
        private long orderId;

        public PointCommand.AddPoint toCommand() {
            return new PointCommand.AddPoint(userId, amount, manual, expireAfterDays, orderId);
        }
    }

    @Getter
    @Setter
    public static class AddCancelPointRequest {
        private long pointId;

        public PointCommand.AddCancelPoint toCommand() {
            return new PointCommand.AddCancelPoint(pointId);
        }
    }

    @Getter
    @Setter
    public static class UsePointRequest {
        @Min(1) @Max(Long.MAX_VALUE)
        private long userId;
        @Min(1) @Max(100000)
        private int amount;
        @Min(1) @Max(Long.MAX_VALUE)
        private long orderId;

        public PointCommand.UsePoint toCommand() {
            return new PointCommand.UsePoint(userId, amount, orderId);
        }
    }

    @Getter
    @Setter
    public static class UseCancelPointRequest {
        @Min(1) @Max(Long.MAX_VALUE)
        private long userId;
        @Min(1) @Max(Long.MAX_VALUE)
        private long pointId;
        @Min(1) @Max(100000)
        private int amount;

        public PointCommand.UseCancelPoint toCommand() {
            return new PointCommand.UseCancelPoint(userId, pointId, amount);
        }
    }

    @Getter
    @Setter
    public static class ExpirePointRequest {
        @Min(value = 1) @Max(Long.MAX_VALUE)
        private long pointId;
        private boolean force;

        public PointCommand.ExpirePoint toCommand() {
            return new PointCommand.ExpirePoint(pointId, force);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class PointMain {
        private final long userId;
        private final int totalRemainingPoints;

        public static PointMain from(PointInfo.PointMain pointInfo) {
            return new PointMain(pointInfo.getUserId(), pointInfo.getTotalRemainingPoints());
        }
    }
}
