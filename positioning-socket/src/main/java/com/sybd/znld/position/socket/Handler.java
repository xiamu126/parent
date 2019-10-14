package com.sybd.znld.position.socket;

import com.sybd.znld.util.MyDateTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;

@Slf4j
public class Handler extends ChannelInboundHandlerAdapter {
    private String fileName = null;
    private File file = null;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        var out = new FileOutputStream(file,true);

        //var writer = new BufferedWriter(new FileWriter(file));

        var result = (ByteBuf) msg;
        var bytes = new byte[result.readableBytes()];
        result.readBytes(bytes);
        log.debug("收到的消息为：" + new String(bytes));

        out.write(bytes);
        out.flush();
        out.close();

        System.out.println(fileName);
        result.release();

        //writer.append(new String(bytes));
        //writer.flush();
        //writer.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        fileName = "./file_"+ MyDateTime.toString(LocalDateTime.now(), MyDateTime.FORMAT3)+".log";
        file = new File(fileName);
        System.out.println("channelActive in server");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}