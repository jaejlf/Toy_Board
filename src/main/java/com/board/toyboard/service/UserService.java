package com.board.toyboard.service;

import com.board.toyboard.model.Role;
import com.board.toyboard.model.User;
import com.board.toyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User save(User user){
        //password
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);

        //role
        Role role = new Role();
        role.setId(1l);
        user.getRoles().add(role);

        return userRepository.save(user);
    }
}
