package com.filling.good.domain.user.service;

import com.filling.good.domain.user.dto.request.LoginRequest;
import com.filling.good.domain.user.dto.request.SignUpRequest;
import com.filling.good.domain.user.exception.DuplicateUserException;
import com.filling.good.domain.user.exception.PasswordErrorException;
import com.filling.good.domain.user.exception.UserNotFoundException;
import com.filling.good.domain.user.dto.response.UserResponse;
import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.enumerate.Job;
import com.filling.good.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackOn = {Exception.class})
    public UserResponse join(SignUpRequest signUpRequest) {
        validateDuplicateUser(signUpRequest);
        User user = new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getNickname(),
                Job.findJobCode(signUpRequest.getJobValue()),
                DEFAULT
        );

        return UserResponse.of(userRepository.save(user));
    }

    @Transactional(rollbackOn = {Exception.class})
    public UserResponse login(LoginRequest loginRequest) {
        User user = getCheckedUser(loginRequest);
        checkPassword(loginRequest, user);

        return UserResponse.of(user);
    }


    /*
    Extract Method
    */

    private void validateDuplicateUser(SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new DuplicateUserException();
        }
    }

    private User getCheckedUser(LoginRequest loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(UserNotFoundException::new);
    }

    private void checkPassword(LoginRequest loginRequest, User user) {
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new PasswordErrorException();
        }
    }

}
