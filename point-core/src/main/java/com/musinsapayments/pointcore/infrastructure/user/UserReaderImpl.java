package com.musinsapayments.pointcore.infrastructure.user;

import com.musinsapayments.pointcore.domain.user.User;
import com.musinsapayments.pointcore.domain.user.UserReader;
import com.musinsapayments.pointcore.exception.user.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.musinsapayments.pointcore.exception.user.UserErrorCode.USER_NOT_EXIST;

@RequiredArgsConstructor
@Component
public class UserReaderImpl implements UserReader {

    private final UserRepository userRepository;

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException(USER_NOT_EXIST, "존재하지 않는 사용자입니다. id: %d".formatted(id)));
    }
}
