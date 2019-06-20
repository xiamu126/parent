package com.sybd.znld.socket.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

public class NioClient {
    public static void main(String[] args) throws IOException {
        var socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        var selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress("localhost", 8899));
        while(true){
            selector.select();
             var keys = selector.selectedKeys();
             keys.forEach(k -> {
                 if(k.isConnectable()){
                     var client = (SocketChannel)k.channel();
                     if(client.isConnectionPending()){
                         try {
                             client.finishConnect();
                             var writeBuffer = ByteBuffer.allocate(1024);
                             writeBuffer.put((LocalDateTime.now()+ ", 连接成功").getBytes());
                             writeBuffer.flip();
                             client.write(writeBuffer);
                             var executorService = Executors.newSingleThreadExecutor();
                             executorService.submit(() -> {
                                 while (true){
                                     var inputStreamReader = new InputStreamReader(System.in);
                                     var bufferedReader = new BufferedReader(inputStreamReader);
                                     var input = bufferedReader.readLine();
                                     writeBuffer.clear();
                                     writeBuffer.put(input.getBytes());
                                     writeBuffer.flip();
                                     client.write(writeBuffer);
                                 }
                             });
                         } catch (IOException e) {
                             e.printStackTrace();
                         }
                     }
                     try {
                         client.register(selector, SelectionKey.OP_READ);
                     } catch (ClosedChannelException e) {
                         e.printStackTrace();
                     }
                 }else if (k.isReadable()){
                     var client = (SocketChannel)k.channel();
                     var readBuffer = ByteBuffer.allocate(1024);
                     try {
                         var count = client.read(readBuffer);
                         if(count > 0){
                             var receivedMessage = new String(readBuffer.array(),0,count);
                             System.out.println(receivedMessage);
                         }
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             });
             keys.clear();
        }
    }
}
