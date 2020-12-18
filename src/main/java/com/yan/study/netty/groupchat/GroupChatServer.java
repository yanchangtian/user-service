package com.yan.study.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

public class GroupChatServer {

    public static void main(String[] args) {

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();

        try {

            ServerBootstrap bootstrap = new ServerBootstrap();



        } catch (Exception e) {

        }

    }

}
