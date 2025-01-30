package com.musinsapayments.pointcore.infrastructure.user;

import com.musinsapayments.pointcore.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
