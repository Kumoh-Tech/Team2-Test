package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.Service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 게시글 작성
    @PostMapping("/write")
    public ResponseEntity<?> boardWrite(@RequestBody Board board, @RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            boardService.write(board, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(board);  // 생성된 게시글 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    // 게시글 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<Board>> boardList(@PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                 @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
        Page<Board> list;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        } else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        return ResponseEntity.ok(list);  // 페이지 목록 반환
    }

    // 게시글 상세 조회
    @GetMapping("/view/{id}")
    public ResponseEntity<?> boardView(@PathVariable("id") Integer id) {
        Board board = boardService.boardView(id);

        if (board != null) {
            return ResponseEntity.ok(board);  // 게시글 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
        }
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> boardDelete(@PathVariable("id") Integer id) {
        try {
            boardService.boardDelete(id);
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 중 오류 발생");
        }
    }

    // 게시글 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<?> boardUpdate(@PathVariable("id") Integer id, @RequestBody Board board, @RequestParam(name = "file", required = false) MultipartFile file) {
        try {
            // 기존 게시글 불러오기
            Board existingBoard = boardService.boardView(id);
            if (existingBoard == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("게시글을 찾을 수 없습니다.");
            }

            // 게시글 정보 수정
            existingBoard.setTitle(board.getTitle());
            existingBoard.setContent(board.getContent());

            // 파일이 있는 경우 처리
            if (file != null && !file.isEmpty()) {
                boardService.write(existingBoard, file);
            } else {
                boardService.write(existingBoard, null);  // 파일 없이 수정
            }

            return ResponseEntity.ok(existingBoard);  // 수정된 게시글 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 중 오류 발생: " + e.getMessage());
        }
    }
}
