package com.boroda.app.model;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.scene.control.TextArea;

public class ChatClientHandler extends SimpleChannelInboundHandler<String> {
    private TextArea textArea;

    public ChatClientHandler(TextArea textArea){
        this.textArea=textArea;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        textArea.appendText(msg + "\n");
    }
}
