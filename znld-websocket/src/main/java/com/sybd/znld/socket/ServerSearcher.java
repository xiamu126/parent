package com.sybd.znld.socket;

import com.sybd.znld.util.MyByte;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ServerSearcher extends Thread {
    private static class SingletonHolder{
        private static final ServerSearcher INSTANCE = new ServerSearcher();
    }
    private ServerSearcher() { }
    public static ServerSearcher getInstance(){
        return SingletonHolder.INSTANCE;
    }

    private final Logger log = LoggerFactory.getLogger(ServerSearcher.class);
    private static final int SEARCHER_PORT = 30202;

    private final List<ServerInfo> serverInfoList = new ArrayList<>();
    private final byte[] buffer = new byte[128];
    private final int minLen = ServerProvider.HEADER.length + 2 + 4;
    private boolean done = false;
    private DatagramSocket ds = null;
    private final CountDownLatch broadcast = new CountDownLatch(1);

    @Override
    public void run() {
        new Thread(() -> {
            //首先监听广播返回数据
            try {
                ds = new DatagramSocket(SEARCHER_PORT);
                var receivedPack = new DatagramPacket(buffer, buffer.length);
                log.debug("UDPSearcher Started, listen on "+SEARCHER_PORT);
                broadcast.countDown(); // 监听准备就绪
                while (!done) {
                    ds.receive(receivedPack);
                    var serverAddress = receivedPack.getAddress().getHostAddress();
                    var port = receivedPack.getPort();
                    var dataLen = receivedPack.getLength();
                    var data = receivedPack.getData();
                    var isValid = dataLen >= minLen && MyByte.startsWith(data, ServerProvider.HEADER);
                    log.debug("UDPSearcher receive from ip:" + serverAddress + "\tport:" + port + "\tdataValid:" + isValid);

                    if (!isValid) continue;

                    var byteBuffer = ByteBuffer.wrap(buffer, ServerProvider.HEADER.length, dataLen);
                    final short cmd = byteBuffer.getShort();
                    final int serverPort = byteBuffer.getInt();
                    if (cmd != 2 || serverPort <= 0) {
                        log.debug("UDPSearcher receive cmd:" + cmd + "\tserverPort:" + serverPort);
                        continue;
                    }

                    var sn = new String(buffer, minLen, dataLen - minLen);
                    var info = new ServerInfo();
                    info.sn = sn;
                    info.port = serverPort;
                    info.address = serverAddress;
                    serverInfoList.add(info);
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
            } finally {
                if (ds != null) { ds.close();ds = null; }
            }
            log.debug("UDPSearcher listener finished.");
        }).start();
        // 接着发送广播包
        try {
            broadcast.await();
            log.debug("sendBroadcast started.");
            var ds = new DatagramSocket();
            var byteBuffer = ByteBuffer.allocate(128);
            byteBuffer.put(ServerProvider.HEADER);// 头部
            byteBuffer.putShort((short) 1);// CMD命名
            byteBuffer.putInt(SEARCHER_PORT);// 回送端口信息
            var requestPacket = new DatagramPacket(byteBuffer.array(),byteBuffer.position() + 1);
            requestPacket.setAddress(InetAddress.getByName("255.255.255.255")); //广播地址
            requestPacket.setPort(ServerProvider.PROVIDER_PORT);// 设置服务器端口
            ds.send(requestPacket);
            ds.close();
            log.debug("sendBroadcast finished.");
        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    public void exit() {
        done = true;
    }

    @Getter @Setter @ToString
    public class ServerInfo {
        public String sn;
        public int port;
        public String address;
    }

    public static void main(String[] args){
        ServerSearcher.getInstance().start();
    }
}
