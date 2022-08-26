package com.filling.good.support;

import com.filling.good.domain.user.entity.User;
import com.filling.good.domain.user.repository.UserRepository;
import com.filling.good.domain.user.service.JwtTokenProvider;
import com.filling.good.global.service.RedisService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.event.annotation.AfterTestClass;

import javax.transaction.Transactional;

import static com.filling.good.domain.user.enumerate.AuthProvider.DEFAULT;
import static com.filling.good.domain.user.enumerate.AuthProvider.GOOGLE;
import static com.filling.good.domain.user.enumerate.Job.STUDENT;

@SpringBootTest
@Transactional
public class CommonServiceTest {

    @Autowired public UserRepository userRepository;
    @Autowired public PasswordEncoder passwordEncoder;
    @Autowired public JwtTokenProvider jwtTokenProvider;
    @Autowired public RedisService redisService;

    public User defaultUser;
    public User googleUser;

    @BeforeEach
    public void setUp() {
        defaultUser = getDefaultUser();
        googleUser = getGoogleUser();

        userRepository.save(defaultUser);
        userRepository.save(googleUser);
    }

    @AfterTestClass
    public void clear() {
        redisService.deleteValues("fillgood@default.com");
        redisService.deleteValues("fillgood@gmail.com");
    }

    public User getDefaultUser() {
        return new User(
                "fillgood@default.com",
                passwordEncoder.encode("{{RAW_PASSWORD}}"),
                "필링굿",
                "필링굿",
                STUDENT,
                DEFAULT
        );
    }

    public User getGoogleUser() {
        return new User(
                "fillgood@gmail.com",
                passwordEncoder.encode("{{RAW_PASSWORD}}"),
                "구글러",
                "구글러",
                STUDENT,
                GOOGLE
        );
    }

}
