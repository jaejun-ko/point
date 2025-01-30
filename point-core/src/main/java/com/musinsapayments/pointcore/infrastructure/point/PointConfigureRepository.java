package com.musinsapayments.pointcore.infrastructure.point;

import com.musinsapayments.pointcore.domain.point.PointConfigure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PointConfigureRepository extends JpaRepository<PointConfigure, Long> {
    Optional<PointConfigure> findFirstByActiveIsTrue();
    List<PointConfigure> findAllByActiveIsTrue();
}
