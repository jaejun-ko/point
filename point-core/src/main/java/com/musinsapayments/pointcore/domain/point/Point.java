package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.common.BaseTimeEntity;
import com.musinsapayments.pointcore.domain.user.User;
import com.musinsapayments.pointcore.exception.point.PointException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_ORDER_ID;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_POINT_AMOUNT;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_POINT_TRANSACTION_TYPE;
import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_USER;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "points")
public class Point extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private int remainingAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PointTransactionType transactionType;

    @Column(nullable = false)
    private boolean manual;

    @Column
    private LocalDate expireAt;

    @Column
    private long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_point_id")
    private Point relatedPoint;

    @OneToMany(mappedBy = "point", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointUsage> pointUsages = new ArrayList<>();

    @Builder
    public Point(
            User user,
            int amount,
            int remainingAmount,
            PointTransactionType transactionType,
            boolean manual,
            LocalDate expireAt,
            long orderId,
            Point relatedPoint
    ) {

        if (user == null) throw new PointException(INVALID_USER);
        if (transactionType == null) throw new PointException(INVALID_POINT_TRANSACTION_TYPE);
        if (transactionType == PointTransactionType.ADD && (amount < 1 || amount > 100000))
            throw new PointException(INVALID_POINT_AMOUNT);
        if (transactionType == PointTransactionType.USE && orderId < 1)
            throw new PointException(INVALID_ORDER_ID);

        this.user = user;
        this.amount = amount;
        this.remainingAmount = remainingAmount;
        this.transactionType = transactionType;
        this.manual = manual;
        this.expireAt = expireAt;
        this.orderId = orderId;
        this.relatedPoint = relatedPoint;
    }

    public boolean isAlreadyUsed() {
        return amount > remainingAmount;
    }

    public void cancel() {
        remainingAmount = 0;
    }

    public void use(int amount) {
        if (remainingAmount - amount < 0) throw new PointException(INVALID_POINT_AMOUNT);

        remainingAmount -= amount;
    }

    public boolean isExpired() {
        return expireAt != null && expireAt.isBefore(LocalDate.now());
    }

    public void expireForce() {
        expireAt = LocalDate.now().plusDays(-1);
    }
}