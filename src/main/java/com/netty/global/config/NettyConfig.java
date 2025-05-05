package com.netty.global.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.server.logging.AccessLog;
import reactor.netty.resources.LoopResources;

import java.util.concurrent.TimeUnit;

@Configuration
public class NettyConfig {

    @Bean
    public WebServerFactoryCustomizer<NettyReactiveWebServerFactory> nettyServerCustomizer() {
        return factory -> factory.addServerCustomizers(httpServer ->
                httpServer
                        // 이벤트 루프 설정: CPU 코어 수 * 2
                        .runOn(LoopResources.create("board-api", 2 * Runtime.getRuntime().availableProcessors(), true))

                        // 연결 타임아웃 설정: 10초
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)

                        // TCP KeepAlive 설정
                        .option(ChannelOption.SO_KEEPALIVE, true)

                        // 요청/응답 로깅 설정
                        .accessLog(true, provider -> AccessLog.create(
                                "{} {} - {} - {}",
                                provider.method(),
                                provider.uri(),
                                provider.status(),
                                provider.requestHeader("User-Agent") != null ? provider.requestHeader("User-Agent") : "Unknown"
                        ))

                        // 커넥션 시 읽기/쓰기 타임아웃 핸들러 추가 (30초)
                        .doOnConnection(conn -> conn
                                .addHandlerLast(new ReadTimeoutHandler(30, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(30, TimeUnit.SECONDS))
                        )
        );
    }
}