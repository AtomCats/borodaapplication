package com.boroda.app.model;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import javafx.scene.control.TextArea;

public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
    private TextArea textArea;
    private ChannelGroup channels;
    private boolean isGroupChat;

    public ChatServerInitializer(TextArea textArea, ChannelGroup channels, boolean isGroupChat) {
        this.textArea = textArea;
        this.channels = channels;
        this.isGroupChat = isGroupChat;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        pipeline.addLast(new ChatServerHandler(textArea, channels, isGroupChat));
    }
}
