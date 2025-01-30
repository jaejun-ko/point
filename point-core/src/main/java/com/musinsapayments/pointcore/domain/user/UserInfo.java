package com.musinsapayments.pointcore.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class UserInfo {

    @Getter
    @RequiredArgsConstructor
    public static class Main {
        private final long id;
        private final String name;
        private final int maxPoints;

        public Main(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.maxPoints = user.getMaxPoints();
        }
    }
}
