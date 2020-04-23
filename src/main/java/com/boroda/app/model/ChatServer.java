package com.boroda.app.model;

import com.boroda.app.controller.MainPageController;
import com.boroda.app.service.ChatSettingsComponent;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Data
@NoArgsConstructor
@Service
public class ChatServer {
    @Autowired
    private MainPageController mainPageController;

    @Autowired
    private ChatSettingsComponent settings;

    private int port;
    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public void run() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer(mainPageController.getChatArea(), channels,
                            settings.isGroupChat()));

            channel = serverBootstrap.bind(port).sync().channel();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) throws InterruptedException {
        final String fullMessage = String.format("[%s] %s \r\n", settings.getNickname(), message);
        channels.writeAndFlush(fullMessage).sync();
    }

    public void shutdownServer() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
