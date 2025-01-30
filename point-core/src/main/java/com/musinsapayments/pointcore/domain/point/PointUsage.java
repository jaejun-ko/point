package com.musinsapayments.pointcore.domain.point;

import com.musinsapayments.pointcore.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "point_usages")
public class PointUsage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    private Point point;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_point_id", nullable = false)
    private Point relatedPoint;

    @Column(nullable = false)
    private int amount;

    @Builder
    public PointUsage(Point point, Point relatedPoint, int amount) {
        this.point = point;
        this.relatedPoint = relatedPoint;
        this.amount = amount;
    }
}
