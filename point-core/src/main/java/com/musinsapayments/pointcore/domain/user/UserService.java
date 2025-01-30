package com.musinsapayments.pointcore.domain.user;

public interface UserService {

    UserInfo.Main registerUser(UserCommand.RegisterUser command);
    UserInfo.Main changeMaxPoints(UserCommand.ChangeMaxPoints command);
    UserInfo.Main getUser(Long id);
}
