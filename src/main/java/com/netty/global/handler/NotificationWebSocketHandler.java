package com.netty.global.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketMessage;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class NotificationWebSocketHandler implements WebSocketHandler {

    private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        sessions.add(session);
        return session.receive() // 클라이언트 메시지는 무시
                .doFinally(signal -> sessions.remove(session))
                .then(); // 연결만 유지
    }

    public void broadcast(String title, String author) {
        Message message = new Message(title, author, System.currentTimeMillis());

        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            for (WebSocketSession session : sessions) {
                WebSocketMessage msg = session.textMessage(jsonMessage);
                session.send(Mono.just(msg)).subscribe();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    static class Message {
        private String title;
        private String author;
        private long timestamp;

        public Message(String title, String author, long timestamp) {
            this.title = title;
            this.author = author;
            this.timestamp = timestamp;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    }
}
