package com.boroda.app.model;

import com.boroda.app.controller.MainPageController;
import com.boroda.app.service.ChatSettingsComponent;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@Service
public class ChatClient {
    @Autowired
    MainPageController mainPageController;

    @Autowired
    ChatSettingsComponent settings;

    private String host = "localhost";
    private int port;
    Channel channel;
    private EventLoopGroup group;

    public Channel run() {
        group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChatClientInitializer(mainPageController.getChatArea()));

            // Start the connection attempt.
            channel = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public void send(String message) throws InterruptedException {
        final String fullMessage = String.format("[%s] %s \r\n", settings.getNickname(), message);
        channel.writeAndFlush(fullMessage).sync();
    }

    public void disconnect() {
        group.shutdownGracefully();
    }
}
