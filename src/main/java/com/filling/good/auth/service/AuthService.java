package com.filling.good.auth.service;

import com.filling.good.auth.dto.request.AuthRequest;
import com.filling.good.auth.dto.response.AuthResponse;
import com.filling.good.auth.exception.ExUserException;
import com.filling.good.user.entity.User;
import com.filling.good.user.enumerate.AuthProvider;
import com.filling.good.user.enumerate.Job;
import com.filling.good.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    @Transactional(rollbackOn = {Exception.class})
    public AuthResponse save(AuthRequest authRequest) {

        if(userRepository.findByEmail(authRequest.getEmail()).isPresent()) {
            throw new ExUserException();
        }
        Job jobCode = Job.findJobCode(authRequest.getJobValue());
        User user = new User(
                authRequest.getEmail(),
                authRequest.getPassword(),
                authRequest.getNickname(),
                jobCode,
                AuthProvider.DEFAULT
        );

        return AuthResponse.res(userRepository.save(user));

    }

}
