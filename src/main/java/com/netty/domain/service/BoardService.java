package com.netty.domain.service;

import com.netty.domain.entity.Board;
import com.netty.global.handler.NotificationWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BoardService {
    private final List<Board> boards = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final NotificationWebSocketHandler notificationWebSocketHandler;

    private BoardService(NotificationWebSocketHandler notificationWebSocketHandler) {
        this.notificationWebSocketHandler = notificationWebSocketHandler;
    }

    public Mono<Board> createBoard(Board board) {
        board.setId(idGenerator.getAndIncrement());
        boards.add(board);
//        notificationWebSocketHandler.broadcast("새로운 게시글이 생성되었습니다. " + board.getTitle()); // 알림 전송
        return Mono.just(board);
    }

    public Flux<Board> getAllBoards() {
        return Flux.fromIterable(boards);
    }

    public Mono<Board> getBoardById(Long id) {
        return Mono.justOrEmpty(boards.stream()
                .filter(board -> board.getId().equals(id))
                .findFirst());
    }

    public Mono<Board> updateBoard(Long id, Board updatedBoard) {
        return getBoardById(id)
                .flatMap(board -> {
                    board.setTitle(updatedBoard.getTitle());
                    board.setContent(updatedBoard.getContent());
                    board.setAuthor(updatedBoard.getAuthor());
                    return Mono.just(board);
                })
                .switchIfEmpty(Mono.empty());
    }

    public Mono<Void> deleteBoard(Long id) {
        boards.removeIf(board -> board.getId().equals(id));
        return Mono.empty();
    }
}