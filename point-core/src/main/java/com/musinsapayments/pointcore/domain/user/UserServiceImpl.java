package com.musinsapayments.pointcore.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserReader userReader;
    private final UserStore userStore;

    @Override
    public UserInfo.Main registerUser(UserCommand.RegisterUser command) {

        var user = command.toEntity();
        var storedUser = userStore.store(user);

        return new UserInfo.Main(storedUser);
    }

    @Override
    public UserInfo.Main changeMaxPoints(UserCommand.ChangeMaxPoints command) {

        var user = userReader.getUser(command.getId());
        user.changeMaxPoints(command.getMaxPoints());
        var storedUser = userStore.store(user);

        return new UserInfo.Main(storedUser);
    }

    @Override
    public UserInfo.Main getUser(Long id) {

        var user = userReader.getUser(id);

        return new UserInfo.Main(user);
    }
}
