package com.sybd.znld.socket;

import com.sybd.znld.util.MyByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.UUID;

public final class ServerProvider extends Thread {
    private final Logger log = LoggerFactory.getLogger(ServerProvider.class);
    public static final int PROVIDER_PORT = 30201;
    public static final byte[] HEADER = "com.sybd.znld.socket".getBytes();

    private static class SingletonHolder{
        private static final ServerProvider INSTANCE = new ServerProvider();
    }
    private ServerProvider() {
        this.sn = UUID.randomUUID().toString().getBytes();
    }
    public static ServerProvider getInstance(){
        return SingletonHolder.INSTANCE;
    }

    public void exit() {
        done = true;
    }

    private final byte[] sn;
    private boolean done = false;
    private DatagramSocket ds = null;
    private final byte[] buffer = new byte[128];

    @Override
    public void run() {
        log.debug("UDPProvider Started, listen on "+PROVIDER_PORT);
        try {
            ds = new DatagramSocket(PROVIDER_PORT);
            var receivedPack = new DatagramPacket(buffer, buffer.length);
            while (!done) {
                ds.receive(receivedPack);
                var clientIp = receivedPack.getAddress().getHostAddress();
                var clientPort = receivedPack.getPort();
                var clientDataLen = receivedPack.getLength();
                var clientData = receivedPack.getData();
                var isValid = clientDataLen >= (HEADER.length + 2 + 4) && MyByte.startsWith(clientData, HEADER);

                log.debug("ServerProvider receive from ip:" + clientIp + "\tport:" + clientPort + "\tdataValid:" + isValid);

                if (!isValid) continue;

                //解析命令与回送端口
                var index = HEADER.length;
                var receivedCmd = (short) ((clientData[index++] << 8) | (clientData[index++] & 0xff));
                var receivedPort = (((clientData[index++]) << 24) | ((clientData[index++] & 0xff) << 16) | ((clientData[index++] & 0xff) << 8) | ((clientData[index] & 0xff)));

                //判断合法性
                if (receivedCmd == 1 && receivedPort > 0) {
                    //构建一份回送数据
                    var byteBuffer = ByteBuffer.wrap(buffer);
                    byteBuffer.put(HEADER);
                    byteBuffer.putShort((short) 2);
                    byteBuffer.putInt(PROVIDER_PORT);
                    byteBuffer.put(sn);
                    var len = byteBuffer.position();
                    //直接根据发送者构建一份回送信息
                    var responsePacket = new DatagramPacket(buffer, len, receivedPack.getAddress(), receivedPort);
                    ds.send(responsePacket);
                    log.debug("ServerProvider response to:" + clientIp + "\tport:" + receivedPort + "\tdataLen:" + len);
                } else {
                    log.debug("ServerProvider receive cmd nonsupport; cmd:" + receivedCmd + "\tport:" + PROVIDER_PORT);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            if (ds != null) {
                ds.close();
                ds = null;
            }
        }
    }

    public static void main(String[] args){
        ServerProvider.getInstance().start();
    }
}
