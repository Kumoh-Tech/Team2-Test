package com.study.board.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    // 게시글 작성/수정 처리
    public Board write(Board board, MultipartFile file) throws Exception {
        if (file != null && !file.isEmpty()) {
            String projectPath = System.getProperty("user.dir") + "/src/main/webapp/";
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            Path savePath = Paths.get(projectPath, fileName);

            // 디렉토리 존재 여부 확인 및 생성
            if (!Files.exists(savePath.getParent())) {
                Files.createDirectories(savePath.getParent());
            }

            try {
                Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new Exception("파일 업로드 실패", e);
            }

            board.setFilename(fileName);
            board.setFilepath("/webapp/" + fileName);
        }

        return boardRepository.save(board);
    }

    // 게시글 목록 조회 처리
    public Page<Board> boardList(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    // 게시글 검색 처리
    public Page<Board> boardSearchList(String searchKeyword, Pageable pageable) {
        return boardRepository.findByTitleContaining(searchKeyword, pageable);
    }

    // 게시글 상세 조회 처리
    public Board boardView(Integer id) {
        return boardRepository.findById(id).orElse(null);
    }

    // 게시글 삭제 처리
    public void boardDelete(Integer id) {
        boardRepository.deleteById(id);
    }
}