package com.musinsapayments.pointcore.infrastructure.user;

import com.musinsapayments.pointcore.domain.user.User;
import com.musinsapayments.pointcore.domain.user.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserStoreImpl implements UserStore {

    private final UserRepository userRepository;

    @Override
    public User store(User user) {
        return userRepository.save(user);
    }
}
