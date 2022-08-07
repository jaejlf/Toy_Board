package com.filling.good.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filling.good.user.enumerate.AuthProvider;
import com.filling.good.user.enumerate.Job;
import com.filling.good.user.enumerate.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Collection;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

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
        this.password = password;
        this.nickname = nickname;
        this.job = job;
        this.authProvider = authProvider;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

}
