# Netty TCP 서버와 메시지 처리 시스템

이 프로젝트는 **Netty** 기반의 TCP 서버를 설정하고, 클라이언트와의 메시지를 처리하는 시스템을 구현하는 예제입니다.  
또한, `MessageRepository`를 사용하여 데이터베이스에 메시지를 저장하는 기능과 클라이언트 간 실시간 메시지 전달 기능을 포함합니다.

## 🎯 목표

- Netty 서버 설정 및 TCP 서버 구현
- 클라이언트와 서버 간의 메시지 송수신
- 메시지 데이터베이스 저장
- 실시간 채팅 시스템 구현

---

## 🛠️ 기술 스택

- **Java 17**
- **Spring Boot**
- **Netty**
- **Reactor (Mono/Flux)**
- **MongoDB** (혹은 관계형 데이터베이스)
- **gRPC** (추후 추가 예정)
- **WebSocket** (추후 추가 예정)

---

## 📦 설치 및 실행 방법

1. **프로젝트 클론**

   ```bash
   git clone https://github.com/yourusername/netty-chat-server.git
   cd netty-chat-server
   ```

2. **필요한 라이브러리 설치**
   ```bash
    ./mvnw clean install
   ```

3. **애플리케이션 실행**
   ```bash
    ./mvnw spring-boot:run
   ```

## ⚙️ 서버 설정

1. **NettyServer**
   Netty 서버는 ServerBootstrap을 사용하여 설정되며, NioEventLoopGroup을 사용해 비동기식 I/O 작업을 처리합니다. 서버는 다음과 같은 역할을 수행합니다:

   - Boss Group: 클라이언트의 연결을 수락하는 역할

   - Worker Group: 클라이언트와 데이터를 송수신하는 역할

```java
public class NettyServer {
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1); // 접속 처리
        EventLoopGroup workerGroup = new NioEventLoopGroup(); // 데이터 처리

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerDomainSocketChannel.class)
                    .childHandler(new ChatServerInitializer());
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("💬 TCP 서버 시작됨. 포트: " + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
```

2. **NettyServer**
   ChatServerInitializer는 각 채널의 파이프라인을 설정하는 역할을 하며, StringDecoder, StringEncoder, 그리고 ChatServerHandler를 추가합니다.

```java
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
    private final MessageRepository messageRepository;

    public ChatServerInitializer(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline().addLast(new StringDecoder(StandardCharsets.UTF_8));
        channel.pipeline().addLast(new StringEncoder(StandardCharsets.UTF_8));
        channel.pipeline().addLast(new ChatServerHandler(messageRepository));
    }
}
```

## 🐞 발생한 오류와 해결 방법

1. **ChatServerHandler에서 MessageRepository 주입 오류**
> 문제

ChatServerHandler가 MessageRepository를 생성자로 받는 구조였는데, ChatServerInitializer에서 인자 없이 ChatServerHandler를 호출하는 방식으로 오류가 발생했습니다.

```bash
'ChatServerHandler(com.netty.domain.repository.MessageRepository)' in 'ChatServerHandler' cannot be applied to '()'
```

> 해결 방법

ChatServerHandler가 MessageRepository를 받도록 생성자가 정의되어 있기 때문에, ChatServerInitializer에서 이를 주입해줘야 합니다.

```java
channel.pipeline().addLast(new ChatServerHandler(messageRepository));
```

1. **channelRead0 메서드 중복 오류**
> 문제

ChatServerHandler에서 channelRead0 메서드가 두 번 정의되어 있어 컴파일 오류가 발생했습니다.

```bash
'channelRead0(ChannelHandlerContext, String)' is already defined in 'com.netty.tcp.ChatServerHandler'
```

> 해결 방법

중복된 channelRead0 메서드를 하나로 합쳐서 처리합니다. 메시지 처리 로직과 클라이언트 간 메시지 전달 로직을 하나의 메서드에 통합했습니다.

```java
@Override
protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    Channel channel = ctx.channel();

    if (!clientMap.containsKey(channel)) {
        Map<String, String> data = objectMapper.readValue(msg, Map.class);
        String username = data.get("username");
        clientMap.put(channel, username);
        channel.writeAndFlush("[서버] 사용자 등록 완료: " + username + "\n");
        return;
    }

    String sender = clientMap.get(channel);
    MessageEntity message = new MessageEntity(sender, msg, System.currentTimeMillis());
    messageRepository.save(message).subscribe();

    for (Channel c : clientMap.keySet()) {
        if (c != channel) {
            c.writeAndFlush("[알림] " + sender + ": " + msg + "\n");
        }
    }
}
```

## 🐞 발생한 오류와 해결 방법

- gRPC 서버 구축: 메시지 송수신을 gRPC로 처리하는 시스템을 추가할 예정입니다.

- WebSocket 지원: 실시간 채팅 기능을 WebSocket으로 처리하여 더 빠른 메시지 전달을 목표로 합니다.

- 게시글에 태그/댓글 추가: 게시글에 태그와 댓글 기능을 추가하여 더 복잡한 상호작용을 구현합니다.

- Prometheus + Grafana 연동: 서버 모니터링을 위한 Prometheus와 Grafana를 연동하여 실시간 데이터를 시각화합니다.

