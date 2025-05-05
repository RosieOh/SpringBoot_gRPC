package com.netty.tcp;

import com.netty.domain.repository.MessageRepository;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.nio.charset.StandardCharsets;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {

    private final MessageRepository messageRepository;

    public ChatServerInitializer(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
        channel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
        channel.pipeline().addLast(new ChatServerHandler(messageRepository)); // ✅ 수정
    }
}
