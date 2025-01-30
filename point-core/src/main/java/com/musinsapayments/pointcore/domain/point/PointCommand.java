package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

public class PointCommand {

    @Getter
    @AllArgsConstructor
    public static class CreatePointConfigure {
        private int maxAmountPerAdd;

        public PointConfigure toEntity() {
            return new PointConfigure(maxAmountPerAdd);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class AddPoint {
        private long userId;
        private int amount;
        private boolean manual;
        private int expireAfterDays;
        private long orderId;

        public Point toEntity(User user) {
            return Point.builder()
                    .user(user)
                    .amount(amount)
                    .remainingAmount(amount)
                    .transactionType(PointTransactionType.ADD)
                    .manual(manual)
                    .expireAt(LocalDate.now().plusDays(expireAfterDays))
                    .orderId(orderId)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class AddCancelPoint {
        private long pointId;
    }

    @Getter
    @AllArgsConstructor
    public static class UsePoint {
        private long userId;
        private int amount;
        private long orderId;

        public Point toEntity(User user) {
            return Point.builder()
                    .user(user)
                    .amount(-amount)
                    .remainingAmount(-amount)
                    .transactionType(PointTransactionType.USE)
                    .manual(false)
                    .expireAt(null)
                    .orderId(orderId)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UseCancelPoint {
        private long userId;
        private long pointId;
        private int amount;
    }

    @Getter
    @AllArgsConstructor
    public static class ExpirePoint {
        private long pointId;
    }
}
