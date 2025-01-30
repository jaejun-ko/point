package com.musinsapayments.pointapi.interfaces.user.dto;

import com.musinsapayments.pointcore.domain.user.UserCommand;
import com.musinsapayments.pointcore.domain.user.UserInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

public class UserDto {

    @Getter
    @Setter
    public static class RegisterRequest {

        @NotBlank
        @Length(min = 1, max = 50)
        private String name;
        @Min(0) @Max(2000000000)
        private int maxPoints;

        public UserCommand.RegisterUser toCommand() {
            return new UserCommand.RegisterUser(name, maxPoints);
        }
    }

    @Getter
    @Setter
    public static class ChangeMaxPointsRequest {

        @Min(1) @Max(Long.MAX_VALUE)
        private Long id;
        @Min(0) @Max(2000000000)
        private int maxPoints;

        public UserCommand.ChangeMaxPoints toCommand() {
            return new UserCommand.ChangeMaxPoints(id, maxPoints);
        }
    }

    @Getter
    @RequiredArgsConstructor
    public static class Main {

        private final Long id;
        private final String name;
        private final int maxPoints;

        public static Main of(UserInfo.Main user) {
            return new Main(user.getId(), user.getName(), user.getMaxPoints());
        }
    }
}
