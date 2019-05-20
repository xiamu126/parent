package com.sybd.znld.socket.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;

public class SocketServer {
    public static void main(String[] args) throws IOException {
        var server = new ServerSocket(2000);

        System.out.println("服务器准备就绪～");
        System.out.println("服务器信息：" + server.getInetAddress() + " P:" + server.getLocalPort());

        for(;;){
            var client = server.accept();
            (new Thread(){
                @Override
                public void run() {
                    super.run();
                    System.out.println("新客户端连接：" + client.getInetAddress() + " P:" + client.getPort());
                    try {
                        var socketOutput = new PrintStream(client.getOutputStream());
                        var socketInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        var goOn = true;
                        do{
                            var str = socketInput.readLine();
                            if(str.equalsIgnoreCase("bye")) {
                                goOn = false;
                                socketOutput.println("bye");
                            }else {
                                System.out.println("客户端请求："+str);
                                socketOutput.println("回送：" + str.toUpperCase());
                            }
                        }while (goOn);
                        socketInput.close();
                        socketOutput.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            client.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("客户端已退出：" + client.getInetAddress() + " P:" + client.getPort());
                }
            }).start();
        }

    }
}
