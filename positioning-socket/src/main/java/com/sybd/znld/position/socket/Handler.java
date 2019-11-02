package com.sybd.znld.position.socket;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.ConnectionFactory;
import com.sybd.znld.util.MyDateTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
public class Handler extends ChannelInboundHandlerAdapter {
    private String fileName = null;
    private File file = null;
    private ConnectionFactory factory = new ConnectionFactory();

    public Handler(){
        factory.setHost("znld-rabbitmq");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin123");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        var out = new FileOutputStream(file,true);

        var result = (String) msg;
        log.debug("收到的消息为：" + result);

        // $GPGGA,074751.00,3108.8731146,N,12039.0113910,E,1,10,1.4,-21.0724,M,8.073,M,99,0000*7D
        if(!result.matches("^\\$GPGGA,.*,.*,.*,.*,.*,.*,.*,.*,.*,.*,.*,.*,.*,.*")){
            return;
        }

        out.write(result.getBytes(StandardCharsets.UTF_8));
        out.write("\r\n".getBytes());// 写入一个换行
        out.flush();
        out.close();

        log.debug(fileName);

        try (var connection = factory.newConnection(); var channel = connection.createChannel()) {
            channel.exchangeDeclare("POSITIONING", BuiltinExchangeType.DIRECT);
            var builder = new AMQP.BasicProperties.Builder();
            var map = new HashMap<String, Object>();
            map.put("filename", fileName);
            channel.basicPublish("POSITIONING", "GPGGA", builder.headers(map).build(), result.getBytes(StandardCharsets.UTF_8));
            log.debug(" [x] Sent '" + "GPGGA" + "':'" + result + "'");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        fileName = MyDateTime.toString(LocalDateTime.now(), MyDateTime.FORMAT3);
        var path = new File("./history/"+MyDateTime.toString(LocalDateTime.now(), MyDateTime.FORMAT4));
        if(!path.exists()){
            path.mkdirs();
        }
        file = new File(path+"/"+fileName);
        log.debug("channelActive in server");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.debug("连接断开："+fileName);
    }
}