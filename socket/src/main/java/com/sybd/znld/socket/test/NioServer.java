package com.sybd.znld.socket.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NioServer {
    private static Map<String, SocketChannel> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        var serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        var serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(8899));
        //创建选择器对象
        var selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true){
            selector.select();
            var keys = selector.selectedKeys();
            keys.forEach(k -> {
                if(k.isAcceptable()){
                    var server = (ServerSocketChannel)k.channel();
                    try {
                        var client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        var uuid = UUID.randomUUID().toString();
                        clients.put(uuid, client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(k.isReadable()){
                    var client = (SocketChannel)k.channel();
                    var readBuffer = ByteBuffer.allocate(1024);
                    try {
                        var count = client.read(readBuffer);
                        if(count > 0){
                            readBuffer.flip();
                            var charset = Charset.forName("UTF-8");
                            var receivedMessage = String.valueOf(charset.decode(readBuffer).array());
                            System.out.println(client + ": "+ receivedMessage);
                            var entry = clients.entrySet().stream().filter(e -> client == e.getValue()).findFirst().orElse(null);
                            if(entry != null){
                                var uuid = entry.getKey();
                                clients.forEach((u, s) -> {
                                    var writeBuffer = ByteBuffer.allocate(1024);
                                    writeBuffer.put((uuid + ": "+ receivedMessage).getBytes());
                                    writeBuffer.flip();
                                    try {
                                        s.write(writeBuffer);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
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
