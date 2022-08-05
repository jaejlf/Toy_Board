package com.filling.good.user.entity;

import com.filling.good.user.enumerate.AuthProvider;
import com.filling.good.user.enumerate.Job;
import com.filling.good.user.enumerate.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue
    private Long userId;
    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private boolean enabled = true;
    private int ban = 0;
    private Long fillPercent = 0L;

    @Enumerated(EnumType.STRING)
    private Job job;

    @Enumerated(EnumType.STRING)
    private Role role = Role.ROLE_USER;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    public User(String email, String password, String nickname, Job job, AuthProvider authProvider) {
        this.email = email;
        this.password = new BCryptPasswordEncoder().encode(password);
        this.nickname = nickname;
        this.job = job;
        this.authProvider = authProvider;
    }

}
