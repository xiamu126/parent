package com.sybd.znld.znld.socket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class UdpProvider {
    public static void main(String[] args) throws IOException {
        var ds = new DatagramSocket(20000);
        var buf = new byte[512];
        var pack = new DatagramPacket(buf, buf.length);
        ds.receive(pack);
        var ip = pack.getAddress().getHostAddress();
        var port = pack.getPort();
        var len = pack.getLength();
        var data = new String(pack.getData(), StandardCharsets.UTF_8);
        System.out.println(data);
        var tmp = new DatagramPacket(data.getBytes(StandardCharsets.UTF_8),data.length(),pack.getAddress(),port);
        ds.send(tmp);
        ds.close();
    }
}
