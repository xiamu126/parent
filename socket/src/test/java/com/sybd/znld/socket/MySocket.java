package com.sybd.znld.socket;

public class MySocket {
    public static void main(String[] args) {
        var str = "$GPGGA,041310.00,3118.7368108,N,12051.3208195,E,1,07,1.6,8.3933,M,8.752,M,99,0000*6A";
        var tmp = str.split(",");
        /*for(var t : tmp){
            System.out.println(t);
        }*/
        System.out.println(tmp[2]+", "+tmp[4]);
    }
}

