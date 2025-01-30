package com.musinsapayments.pointcore.infrastructure.point;

import com.musinsapayments.pointcore.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {

    @Query("SELECT p FROM Point p " +
            "WHERE p.user.id = :userId " +
            "AND p.transactionType = 'ADD' " +
            "AND p.remainingAmount > 0 " +
            "AND p.expireAt > CURRENT_TIMESTAMP " +
            "ORDER BY p.manual DESC, p.expireAt ASC")
    List<Point> findRemainingPoints(Long userId);
}
