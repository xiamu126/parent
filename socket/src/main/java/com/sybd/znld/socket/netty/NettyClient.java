package com.sybd.znld.socket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        var loop = new NioEventLoopGroup();
        try{
            var bootstrap = new Bootstrap();
            bootstrap.group(loop)
                    .channel(NioSocketChannel.class)
                    .handler(new NettyClientHandler());
            var channelFuture = bootstrap.connect("192.168.11.101", 8899).sync();
            channelFuture.channel().writeAndFlush("test");
            channelFuture.channel().closeFuture().sync();
        }finally {
            loop.shutdownGracefully();
        }

    }
}
