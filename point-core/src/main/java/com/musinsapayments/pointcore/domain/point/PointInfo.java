package com.musinsapayments.pointcore.domain.point;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class PointInfo {

    @Getter
    @RequiredArgsConstructor
    public static class PointConfigureMain {
        private long id;
        private int maxAmountPerAdd;

        public PointConfigureMain(PointConfigure pointConfigure) {
            this.id = pointConfigure.getId();
            this.maxAmountPerAdd = pointConfigure.getMaxAmountPerAdd();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class PointMain {
        private final long userId;
        private final int totalRemainingPoints;
    }
}
