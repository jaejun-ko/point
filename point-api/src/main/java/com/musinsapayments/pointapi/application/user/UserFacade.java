package com.musinsapayments.pointapi.application.user;

import com.musinsapayments.pointcore.domain.user.UserCommand;
import com.musinsapayments.pointcore.domain.user.UserInfo;
import com.musinsapayments.pointcore.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserFacade {

    private final UserService userService;

    public UserInfo.Main registerUser(UserCommand.RegisterUser command) {

        return userService.registerUser(command);
    }

    public UserInfo.Main changeMaxPoints(UserCommand.ChangeMaxPoints command) {

        return userService.changeMaxPoints(command);
    }

    public UserInfo.Main getUser(Long id) {

        return userService.getUser(id);
    }
}
