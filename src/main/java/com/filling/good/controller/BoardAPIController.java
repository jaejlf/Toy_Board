package com.filling.good.controller;

import com.filling.good.model.Board;
import com.filling.good.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api")
class BoardAPIController {

    @Autowired
    private BoardRepository repository;

    // 제목 또는 내용으로 검색
    @GetMapping("/boards")
    List<Board> all(
            @RequestParam(required = false, defaultValue = "") String title,
            @RequestParam(required = false, defaultValue = "") String content) {

        if (StringUtils.isEmpty(title) && StringUtils.isEmpty(content)) {
            return repository.findAll(); //제목 또는 내용이 전달되지 않았을 때 -> 전체 데이터 리턴
        } else {
            return repository.findByTitleOrContent(title, content);
        }

    }

    // 게시글 저장 (title + content)
    @PostMapping("/boards")
    Board newBoard(@RequestBody Board newBoard) {
        return repository.save(newBoard);
    }

    // 개별 아이템 검색
    @GetMapping("/boards/{id}")
    Board one(@PathVariable Long id) {

        return repository.findById(id).orElse(null);
    }

    // 게시글 수정 (or 새로 저장)
    @PutMapping("/boards/{id}")
    Board replaceBoard(@RequestBody Board newBoard, @PathVariable Long id) {

        return repository.findById(id)
                .map(board -> {
                    board.setTitle(newBoard.getTitle());
                    board.setContent(newBoard.getContent());
                    return repository.save(board);
                })
                .orElseGet(() -> {
                    newBoard.setId(id);
                    return repository.save(newBoard);
                });
    }

    // 게시글 삭제
    @Secured("ROLE_ADMIN") // ROLE_ADMIN 사용자만 DELETE 요청할 수 있도록
    @DeleteMapping("/boards/{id}")
    void deleteBoard(@PathVariable Long id) {
        repository.deleteById(id);
    }

}