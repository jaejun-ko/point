package com.musinsapayments.pointcore.domain.user;

import com.musinsapayments.pointcore.common.BaseTimeEntity;
import com.musinsapayments.pointcore.exception.user.UserException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.musinsapayments.pointcore.exception.user.UserErrorCode.INVALID_MAX_POINTS;
import static com.musinsapayments.pointcore.exception.user.UserErrorCode.INVALID_NAME;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int maxPoints;

    public User(String name, int maxPoints) {

        if (name == null || name.isBlank() || name.length() > 50) throw new UserException(INVALID_NAME);
        if (maxPoints < 0 || maxPoints > 2000000000) throw new UserException(INVALID_MAX_POINTS);

        this.name = name;
        this.maxPoints = maxPoints;
    }

    public void changeMaxPoints(int maxPoints) {

        if (maxPoints < 0 || maxPoints > 2000000000) throw new UserException(INVALID_MAX_POINTS);

        this.maxPoints = maxPoints;
    }
}
