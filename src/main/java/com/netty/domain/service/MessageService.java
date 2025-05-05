package com.netty.domain.service;

import com.netty.domain.entity.MessageEntity;
import com.netty.domain.repository.MessageRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Mono<MessageEntity> saveMessage(MessageEntity messageEntity) {
        return messageRepository.save(messageEntity);
    }
}