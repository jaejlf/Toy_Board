package com.filling.good.auth.service;

import com.filling.good.auth.dto.request.LoginRequest;
import com.filling.good.auth.dto.request.SignUpRequest;
import com.filling.good.auth.exception.DuplicateUserException;
import com.filling.good.auth.exception.PasswordErrorException;
import com.filling.good.auth.exception.UserNotFoundException;
import com.filling.good.user.dto.response.UserResponse;
import com.filling.good.user.entity.User;
import com.filling.good.user.enumerate.AuthProvider;
import com.filling.good.user.enumerate.Job;
import com.filling.good.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackOn = {Exception.class})
    public UserResponse join(SignUpRequest signUpRequest) {

        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new DuplicateUserException();
        }

        //Job jobCode = Job.findJobCode(signUpRequest.getJobValue());
        User user = new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getNickname(),
                Job.findJobCode(signUpRequest.getJobValue()),
                AuthProvider.DEFAULT
        );

        return UserResponse.res(userRepository.save(user));

    }

    @Transactional(rollbackOn = {Exception.class})
    public User login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new PasswordErrorException();
        }

        return user;

    }

}
