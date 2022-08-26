package com.filling.good.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.filling.good.domain.user.enumerate.AuthProvider;
import com.filling.good.domain.user.enumerate.Job;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String password;

    @Email
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String name;
    private boolean enabled = true;
    private int ban = 0;
    private Long fillPercent = 0L;

    @Enumerated(EnumType.STRING)
    private Job job;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    public User(String email, String password, String nickname, String name, Job job, AuthProvider authProvider) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.name = name;
        this.job = job;
        this.authProvider = authProvider;
        this.roles = Collections.singletonList("ROLE_USER");
    }

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
