package com.musinsapayments.pointcore.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class UserCommand {

    @Getter
    @AllArgsConstructor
    public static class RegisterUser {
        private String name;
        private int maxPoints;

        public User toEntity() {
            return new User(name, maxPoints);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ChangeMaxPoints {
        private long id;
        private int maxPoints;
    }
}
