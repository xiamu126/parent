package com.sybd.znld.position.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Server {
    public static void main(String[] args) throws InterruptedException, IOException, TimeoutException {
        var bossGroup = new NioEventLoopGroup();
        var workerGroup = new NioEventLoopGroup();
        try{
            var serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //.option(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            var pipeline = socketChannel.pipeline();
                            // 按行的方式来分包
                            pipeline.addLast(new LineBasedFrameDecoder(1024)); // 1kb
                            pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                            //pipeline.addLast(new LoginHandler());
                            pipeline.addLast(new Handler());
                            pipeline.addLast(new IdleStateHandler(0, 60, 0, TimeUnit.SECONDS));
                        }
                    });
            log.debug("Server启动");
            var channelFuture = serverBootstrap.bind(8899).sync();
            channelFuture.channel().closeFuture().sync();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

