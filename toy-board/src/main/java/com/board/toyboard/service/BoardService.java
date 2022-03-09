package com.board.toyboard.service;

import com.board.toyboard.model.Board;
import com.board.toyboard.model.User;
import com.board.toyboard.repository.BoardRepository;
import com.board.toyboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public Board save(String username, Board board){
        User user = userRepository.findByUsername(username);
        board.setUser(user);
        return boardRepository.save(board);
    }
}
