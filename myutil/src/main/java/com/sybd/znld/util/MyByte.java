package com.sybd.znld.util;

public class MyByte {
    public static boolean startsWith(byte[] source, byte[] dest) {
        return startsWith(source, 0, dest);
    }
    public static boolean startsWith(byte[] source, int offset, byte[] dest) {

        if (dest.length > (source.length - offset)) {
            return false;
        }

        for (var i = 0; i < dest.length; i++) {
            if (source[offset + i] != dest[i]) {
                return false;
            }
        }
        return true;
    }
    public static boolean equals(byte[] source, byte[] dest) {

        if (dest.length != source.length) {
            return false;
        }
        return startsWith(source, 0, dest);
    }
}
