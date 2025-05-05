package com.netty.domain.repository;

import com.netty.domain.entity.MessageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MessageRepository extends ReactiveCrudRepository<MessageEntity, Long> {
    Mono<MessageEntity> findByUsername(String username);
}
