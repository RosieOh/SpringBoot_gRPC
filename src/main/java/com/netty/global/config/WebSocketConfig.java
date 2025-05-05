package com.netty.global.config;

import com.netty.global.handler.NotificationWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class WebSocketConfig {

    @Bean
    public HandlerMapping handlerMapping(NotificationWebSocketHandler webSocketHandler) {
        return new SimpleUrlHandlerMapping(Map.of(
                "/ws/notifications", webSocketHandler
        ), -1); // order 우선순위
    }
}
