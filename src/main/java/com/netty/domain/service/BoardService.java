package com.netty.domain.service;

import com.netty.domain.entity.Board;
import com.netty.domain.repository.BoardRepository;
import com.netty.global.handler.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final NotificationWebSocketHandler notificationWebSocketHandler;

    public Mono<Board> createBoard(Board board) {
        return boardRepository.save(board)
                .doOnSuccess(saved -> notificationWebSocketHandler.broadcast(saved.getTitle(), saved.getAuthor()));
    }

    public Flux<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Mono<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    public Mono<Board> updateBoard(Long id, Board updatedBoard) {
        return boardRepository.findById(id)
                .flatMap(existing -> {
                    existing.setTitle(updatedBoard.getTitle());
                    existing.setContent(updatedBoard.getContent());
                    existing.setAuthor(updatedBoard.getAuthor());
                    return boardRepository.save(existing);
                });
    }

    public Mono<Void> deleteBoard(Long id) {
        return boardRepository.deleteById(id);
    }
}
