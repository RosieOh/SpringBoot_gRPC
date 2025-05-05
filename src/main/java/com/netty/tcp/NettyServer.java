package com.netty.tcp;

import com.netty.domain.repository.MessageRepository;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerDomainSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

// TCP 서버 부트스트랩을 설정한다.
public class NettyServer {

    private final int port;
    private final MessageRepository messageRepository;

    public NettyServer(int port, MessageRepository messageRepository) {
        this.port = port;
        this.messageRepository = messageRepository;
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 여기 잘못돼있음! 수정 필요 👇
                    .childHandler(new ChatServerInitializer(messageRepository));

            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("💬 TCP 서버 시작됨. 포트: " + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}