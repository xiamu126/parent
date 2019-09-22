package com.sybd.znld.util;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class MySignature {
    public static String generate(final Map<String, String> data, String secretKey) throws NoSuchAlgorithmException {
        var keySet = data.keySet();
        var keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        var sb = new StringBuilder();
        for (var k : keyArray) {
            if (data.get(k).trim().length() > 0) // 参数值为空，则不参与签名
                sb.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        sb.append("secretKey=").append(secretKey);
        return MD5.encrypt(sb.toString()).toUpperCase();
    }
}
