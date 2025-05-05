package com.netty.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("messages")  // 테이블 명을 지정
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {

    @Id
    private Long id;

    private String username;
    private String content;
    private Long timestamp;

    public MessageEntity(String username, String content, Long timestamp) {
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }
}
