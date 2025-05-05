package com.netty.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class NotificationWebSocketHandler implements WebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessions.add(session);
        log.info("Client connected: {}", session.getId());

        return session.receive()
                .doOnNext(message -> log.info("Received: {}", message.getPayloadAsText()))
                .doFinally(signal -> {
                    sessions.remove(session);
                    log.info("Client disconnected: {}", session.getId());
                })
                .then(); // 수신만 처리
    }

    public void broadcast(String title, String author) {
        try {
            String json = objectMapper.writeValueAsString(
                    new NotificationMessage("새 게시글", title, author)
            );

            WebSocketMessage message = sessions.stream()
                    .findAny() // any 하나 꺼내서 session.getTextMessage 만들어야 함
                    .map(session -> session.textMessage(json))
                    .orElse(null);

            if (message != null) {
                Flux.fromIterable(sessions)
                        .flatMap(session -> session.send(Mono.just(session.textMessage(json))))
                        .subscribe();
            }
        } catch (Exception e) {
            log.error("Broadcast error: {}", e.getMessage());
        }
    }

    private record NotificationMessage(String type, String title, String author) {}
}
