package com.netty.tcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netty.domain.entity.MessageEntity;
import com.netty.domain.repository.MessageRepository;
import io.netty.channel.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<Channel, String> clientMap = new ConcurrentHashMap<>();
    private static final Set<Channel> clients = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final MessageRepository messageRepository;

    public ChatServerHandler(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();

        if (!clientMap.containsKey(channel)) {
            // 처음 받은 메시지를 username 등록으로 처리
            Map<String, String> data = objectMapper.readValue(msg, Map.class);
            String username = data.get("username");
            clientMap.put(channel, username);
            channel.writeAndFlush("[서버] 사용자 등록 완료: " + username + "\n");
            return;
        }

        String sender = clientMap.get(channel);
        MessageEntity message = new MessageEntity(sender, msg, System.currentTimeMillis());

        // DB 저장 (Reactive)
        messageRepository.save(message).subscribe();

        for (Channel c : clientMap.keySet()) {
            if (c != channel) {
                c.writeAndFlush("[알림] " + sender + ": " + msg + "\n");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        clients.add(ctx.channel());
        System.out.println("📢 클라이언트 접속: " + ctx.channel().remoteAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        clients.remove(ctx.channel());
        System.out.println("❌ 클라이언트 연결 종료: " + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
