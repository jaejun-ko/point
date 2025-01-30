package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.common.BaseTimeEntity;
import com.musinsapayments.pointcore.exception.point.PointException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.musinsapayments.pointcore.exception.point.PointErrorCode.INVALID_POINT_AMOUNT;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "point_configures")
public class PointConfigure extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private int maxAmountPerAdd;

    @Column(nullable = false)
    private boolean active = true;

    public PointConfigure(int maxAmountPerAdd) {
        if (maxAmountPerAdd < 1 || maxAmountPerAdd > 100000) throw new PointException(INVALID_POINT_AMOUNT);

        this.maxAmountPerAdd = maxAmountPerAdd;
    }

    public void changeMaxAmountPerAdd(int maxAmountPerAdd) {
        if (maxAmountPerAdd < 1 || maxAmountPerAdd > 100000) throw new PointException(INVALID_POINT_AMOUNT);

        this.maxAmountPerAdd = maxAmountPerAdd;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }
}
