package com.sybd.znld.onenet;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;


/**
 * 功能描述: OneNet数据推送接收程序工具类。
 *
 * Created by Roy on 2017/5/17.
 *
 */
@Slf4j
public class Util {
    private static MessageDigest mdInst;

    static {
        try {
            mdInst = MessageDigest.getInstance("MD5");
            Security.addProvider(new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException ex) {
            log.error(ex.getMessage());
        }
    }

    /**
     * 功能描述:在OneNet平台配置数据接收地址时，平台会发送URL&token验证请求<p>
     *          使用此功能函数验证token
     * @param msg 请求参数 <msg>的值
     * @param nonce 请求参数 <nonce>的值
     * @param signature 请求参数 <signature>的值
     * @param token OneNet平台配置页面token的值
     * @return token检验成功返回true；token校验失败返回false
     */
    public static boolean checkToken(String msg,String nonce,String signature, String token) {
        var paramB = new byte[token.length() + 8 + msg.length()];
        System.arraycopy(token.getBytes(), 0, paramB, 0, token.length());
        System.arraycopy(nonce.getBytes(), 0, paramB, token.length(), 8);
        System.arraycopy(msg.getBytes(), 0, paramB, token.length() + 8, msg.length());
        var sig = Base64.encodeBase64String(mdInst.digest(paramB));
        log.info("url&token validation: result {},  detail receive:{} calculate:{}", sig.equals(signature.replace(' ','+')),signature,sig);
        return sig.equals(signature.replace(' ','+'));
    }

    /**
     * 功能描述: 检查接收数据的信息摘要是否正确。<p>
     *          方法非线程安全。
     * @param obj 消息体对象
     * @param token OneNet平台配置页面token的值
     * @return
     */
    public static boolean checkSignature(BodyObj obj, String token)  {
        //计算接受到的消息的摘要
        //token长度 + 8B随机字符串长度 + 消息长度
        var signature = new byte[token.length() + 8 + obj.getMsg().toString().length()];
        System.arraycopy(token.getBytes(), 0, signature, 0, token.length());
        System.arraycopy(obj.getNonce().getBytes(), 0, signature, token.length(), 8);
        System.arraycopy(obj.getMsg().toString().getBytes(), 0, signature, token.length() + 8, obj.getMsg().toString().length());
        var calSig = Base64.encodeBase64String(mdInst.digest(signature));
        log.info("check signature: result:{}  receive sig:{},calculate sig: {}",calSig.equals(obj.getMsgSignature()),obj.getMsgSignature(),calSig);
        return calSig.equals(obj.getMsgSignature());
    }

    /**
     *  功能描述 解密消息
     * @param obj 消息体对象
     * @param encodeKey OneNet平台第三方平台配置页面为用户生成的AES的BASE64编码格式秘钥
     */
    public static String decryptMsg(BodyObj obj, String encodeKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        var encMsg = Base64.decodeBase64(obj.getMsg().toString());
        var aeskey = Base64.decodeBase64(encodeKey + "=");
        SecretKey secretKey = new SecretKeySpec(aeskey, 0, 32, "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(aeskey, 0, 16));
        var allmsg = cipher.doFinal(encMsg);
        var msgLenBytes = new byte[4];
        System.arraycopy(allmsg, 16, msgLenBytes, 0, 4);
        var msgLen = getMsgLen(msgLenBytes);
        var msg = new byte[msgLen];
        System.arraycopy(allmsg, 20, msg, 0, msgLen);
        return new String(msg);
    }

    /**
     * 功能描述 解析数据推送请求，生成code>BodyObj</code>消息对象
     * @param body 数据推送请求body部分
     * @param encrypted 表征是否为加密消息
     * @return  生成的<code>BodyObj</code>消息对象
     */
    public static BodyObj resolveBody(String body, boolean encrypted) {
        var jsonMsg = new JSONObject(body);
        var obj = new BodyObj();
        obj.setNonce(jsonMsg.getString("nonce"));
        obj.setMsgSignature(jsonMsg.getString("msg_signature"));
        if (encrypted) {
            if (!jsonMsg.has("enc_msg")) {
                return null;
            }
            obj.setMsg(jsonMsg.getString("enc_msg"));
        } else {
            if (!jsonMsg.has("msg")) {
                return null;
            }
            obj.setMsg(jsonMsg.get("msg"));
        }
        return obj;
    }

    private static int getMsgLen(byte[] arrays) {
        var len = 0;
        len += (arrays[0] & 0xFF) << 24;
        len += (arrays[1] & 0xFF) << 16;
        len += (arrays[2] & 0xFF) << 8;
        len += (arrays[3] & 0xFF);
        return len;
    }

    @Getter @Setter
    public static class BodyObj {
        public Object msg;
        public String nonce;
        public String msgSignature;

        public String toString(){
            return "{ \"msg\":"+this.msg+"，\"nonce\":"+this.nonce+"，\"signature\":"+this.msgSignature+"}";
        }
    }
}
