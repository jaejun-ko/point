package com.musinsapayments.pointcore.infrastructure.point;

import com.musinsapayments.pointcore.domain.point.PointUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointUsageRepository extends JpaRepository<PointUsage, Long> {

    @Query(
            "SELECT pu FROM PointUsage pu" +
                    " JOIN FETCH pu.point" +
                    " JOIN FETCH pu.relatedPoint " +
                    " WHERE pu.point.id = :pointId" +
                    " AND pu.relatedPoint.amount - pu.relatedPoint.remainingAmount > 0"
    )
    List<PointUsage> findCancellablePointUsages(long pointId);
}
