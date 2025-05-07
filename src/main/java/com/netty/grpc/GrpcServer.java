package com.netty.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new ChatServiceImpl())
                .build();

        System.out.println("gRPC 서버 시작...");
        server.start();
        server.awaitTermination();
    }
}
