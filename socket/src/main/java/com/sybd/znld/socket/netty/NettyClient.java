package com.sybd.znld.socket.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {
    public static void main(String[] args) throws InterruptedException {
        var loop = new NioEventLoopGroup();
        try{
            var bootstrap = new Bootstrap();
            bootstrap.group(loop).channel(NioSocketChannel.class).handler(new NettyClientHandler());
            var channelFuture = bootstrap.connect("localhost", 8899).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            loop.shutdownGracefully();
        }

    }
}
