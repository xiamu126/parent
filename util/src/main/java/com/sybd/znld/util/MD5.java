package com.sybd.znld.util;

import com.google.common.io.BaseEncoding;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String encrypt(String text) throws NoSuchAlgorithmException {
        var md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes());
        var digest = md.digest();
        return BaseEncoding.base16().encode(digest);
    }
    public static String encrypt(String text, int count) throws NoSuchAlgorithmException {
        var result = encrypt(text);
        for(int i = 0; i < count - 1; i++){
            result = encrypt(result);
        }
        return result;
    }
    public static String encrypt(String text, String salt) throws NoSuchAlgorithmException {
        var md = MessageDigest.getInstance("MD5");
        md.update(salt.getBytes());
        md.update(text.getBytes());
        var digest = md.digest();
        return BaseEncoding.base16().encode(digest);
    }

    public static boolean verify(String text, String md5) throws NoSuchAlgorithmException {
        var md5Text = encrypt(text);
        return md5Text.equalsIgnoreCase(md5);
    }

    public static void main(String[] args){
        try {
            System.out.println(encrypt(encrypt("2019").toLowerCase()).toLowerCase());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
