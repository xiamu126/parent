package com.sybd.znld.znld.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketClient {
    public static void main(String[] args) throws IOException {
        var socket = new Socket();
        socket.setSoTimeout(3000);
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(), 3333), 3000);

        System.out.println("已发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + " P:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " P:" + socket.getPort());

        var input = new BufferedReader(new InputStreamReader(System.in));
        var socketPrintStream = new PrintStream(socket.getOutputStream());
        var socketBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        var goOn = true;
        do{
            var str = input.readLine();
            socketPrintStream.println(str);
            var ret = socketBufferedReader.readLine();
            if(ret.equalsIgnoreCase("bye")) goOn = false;
            else System.out.println("服务端返回："+ret);
        }while (goOn);

        socketBufferedReader.close();
        socketPrintStream.close();
        input.close();
        socket.close();
    }
}
