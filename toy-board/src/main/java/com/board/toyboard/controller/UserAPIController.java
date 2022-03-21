package com.board.toyboard.controller;

import com.board.toyboard.model.Board;
import com.board.toyboard.model.User;
import com.board.toyboard.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
class UserAPIController {

    @Autowired
    private UserRepository repository;

    // 유저 검색 (현재 - 조건 X)
    @GetMapping("/users")
    List<User> all(@RequestParam(required = false) String method, @RequestParam(required = false) String text) {
        List<User> users = null;

        if ("query".equals(method)) { // Custom Query #1. GET /api/users?method=query&text=
            users = repository.findByUsernameQuery(text);
        } else if ("nativeQuery".equals(method)) { // Custom Query #2. GET /api/users?method=nativeQuery&text=
            users = repository.findByUsernameNativeQuery(text);
        } else {
            users = repository.findAll();
        }

        return users;
    }

    // 유저 정보 저장 (title + content)
    @PostMapping("/users")
    User newUser(@RequestBody User newUser) {
        return repository.save(newUser);
    }

    // 유저 정보 검색
    @GetMapping("/users/{id}")
    User one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);
    }

    // 유저 정보 수정 (or 새로 저장)
    @PutMapping("/users/{id}")
    User replaceUser(@RequestBody User newUser, @PathVariable Long id) {

        return repository.findById(id)
                .map(user -> {
                    //user.setTitle(newUser.getTitle());
                    //user.setContent(newUser.getContent());
                    //user.setBoards(newUser.getBoards());
                    user.getBoards().clear();
                    user.getBoards().addAll(newUser.getBoards());
                    for (Board board : user.getBoards()) {
                        board.setUser(user);
                    }
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(id);
                    return repository.save(newUser);
                });
    }

    // 유저 정보 삭제
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {
        repository.deleteById(id);
    }

}