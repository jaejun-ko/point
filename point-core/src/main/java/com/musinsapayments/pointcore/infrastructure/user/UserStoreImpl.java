package com.musinsapayments.pointcore.infrastructure.user;

import com.musinsapayments.pointcore.domain.user.User;
import com.musinsapayments.pointcore.domain.user.UserStore;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Component;

import static com.musinsapayments.pointcore.configuration.CacheConfiguration.USER_CACHE;

@RequiredArgsConstructor
@Component
public class UserStoreImpl implements UserStore {

    private final UserRepository userRepository;

    @CachePut(cacheNames = USER_CACHE, key = "'user:' + #user.id")
    @Override
    public User store(User user) {

        return userRepository.save(user);
    }
}
