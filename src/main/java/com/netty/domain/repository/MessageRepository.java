package com.netty.domain.repository;

import com.netty.domain.entity.MessageEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MessageRepository extends ReactiveCrudRepository<MessageEntity, Long> {
}
