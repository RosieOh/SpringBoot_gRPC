package com.netty.grpc;

import com.netty.domain.Chat;
import com.netty.domain.ChatServiceGrpc;
import io.grpc.stub.StreamObserver;

public class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

    @Override
    public void sendMessage(Chat.ChatMessage request, StreamObserver<Chat.ChatResponse> responseObserver) {
        // 요청 메시지 처리
        String responseText = "Hello, " + request.getUsername() + "! You said: " + request.getMessage();
        Chat.ChatResponse response = Chat.ChatResponse.newBuilder()
                .setMessage(responseText)
                .build();

        // 응답 전송
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getMessages(Chat.Empty request, StreamObserver<Chat.ChatMessage> responseObserver) {
        // 스트림 메시지 전송
        for (int i = 0; i < 5; i++) {
            Chat.ChatMessage message = Chat.ChatMessage.newBuilder()
                    .setUsername("Server")
                    .setMessage("Message " + (i + 1))
                    .setTimestamp(System.currentTimeMillis())
                    .build();

            responseObserver.onNext(message);
        }
        responseObserver.onCompleted();
    }
}
