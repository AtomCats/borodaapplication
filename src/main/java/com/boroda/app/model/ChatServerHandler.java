package com.boroda.app.model;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import javafx.scene.control.TextArea;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    private ChannelGroup channels;
    private boolean isGroupChat;
    private TextArea textArea;

    public ChatServerHandler(TextArea textArea, ChannelGroup channels, boolean isGroupChat) {
        this.textArea = textArea;
        this.channels = channels;
        this.isGroupChat = isGroupChat;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (!isGroupChat && channels.size() > 1) {
            ctx.close();
        } else {
            super.channelActive(ctx);
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        final Channel incoming = ctx.channel();
        final StringBuffer userJoined =  new StringBuffer("[SERVER] - " + incoming.remoteAddress() + " has joined!\n");
        for (Channel channel : channels) {
            channel.writeAndFlush(userJoined.toString()).sync();
        }
        textArea.appendText(userJoined.toString());
        channels.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        final StringBuffer userLeft =  new StringBuffer("[SERVER] - " + incoming.remoteAddress() + " has left!\n");

        for (Channel channel : channels) {
            channel.writeAndFlush(userLeft.toString()).sync();
        }
        textArea.appendText(userLeft.toString());
        channels.remove(ctx.channel());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel incoming = ctx.channel();
        final String message =  msg + "\n";

        for (Channel channel : channels) {
            if (channel != incoming) {
                channel.writeAndFlush(message).sync();
            }
        }
        textArea.appendText(message);
    }
}
