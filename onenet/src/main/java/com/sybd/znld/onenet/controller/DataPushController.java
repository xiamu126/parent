package com.sybd.znld.onenet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.onenet.Util;
import com.sybd.znld.onenet.config.MyWebSocketHandler;
import com.sybd.znld.onenet.controller.dto.News;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
public class DataPushController {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    public OneNetResourceMapper oneNetResourceMapper;

    private static String token ="abcdefghijkmlnopqrstuvwxyz";//用户自定义token和OneNet第三方平台配置里的token一致
    private static String aeskey ="whBx2ZwAU5LOHVimPj1MPx56QRe3OsGGWRe4dr17crV";//aeskey和OneNet第三方平台配置里的token一致
    /**
     * 功能描述：第三方平台数据接收。<p>
     *           <ul>注:
     *               <li>1.OneNet平台为了保证数据不丢失，有重发机制，如果重复数据对业务有影响，数据接收端需要对重复数据进行排除重复处理。</li>
     *               <li>2.OneNet每一次post数据请求后，等待客户端的响应都设有时限，在规定时限内没有收到响应会认为发送失败。
     *                    接收程序接收到数据时，尽量先缓存起来，再做业务逻辑处理。</li>
     *           </ul>
     * @param body 数据消息
     * @return 任意字符串。OneNet平台接收到http 200的响应，才会认为数据推送成功，否则会重发。
     */
    @RequestMapping(value = "/receive",method = RequestMethod.POST)
    @ResponseBody
    public String receive(@RequestBody String body) {
        log.info("data receive:  body String --- " +body);
        /************************************************
         *  解析数据推送请求，非加密模式。
         *  如果是明文模式使用以下代码
         **************************************************/
        /*************明文模式  start****************/
        var obj = Util.resolveBody(body, false);
        log.info("data receive:  body Object --- " +obj);
        if (obj != null){
            var dataRight = Util.checkSignature(obj, token);
            if (dataRight){
                String ds = null;
                Object value = null;
                Long at = null;
                try{
                    ds = JsonPath.read(body,"$.msg.ds_id");
                }catch (Exception ex){
                    log.debug(ex.getMessage());
                }
                try{
                    value = JsonPath.read(body,"$.msg.value");
                }catch (Exception ex){
                    log.debug(ex.getMessage());
                }
                try{
                    at = JsonPath.read(body,"$.msg.at");
                }catch (Exception ex){
                    log.debug(ex.getMessage());
                }
                if(ds != null){ // 为空时可能是其它数据，如登入
                    var ids = ds.split("_");
                    if(ids.length == 3){
                        var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids[0], ids[1], ids[2]);
                        if(name != null){ // 有些DataStreamId是没有定义的，如北斗定位的误差之类
                            if(value != null){
                                log.debug("当前获取的资源为："+name+";数值为"+value);
                                if(at != null){
                                    log.debug("更新时间为"+ MyDateTime.toString(at, MyDateTime.format1));
                                }else{
                                    log.debug("更新时间为空");
                                }
                                var news = new News();
                                news.name = name;
                                news.value = value;
                                news.at = at;
                                var objectMapper = new ObjectMapper();
                                try {
                                    MyWebSocketHandler.sendAll(objectMapper.writeValueAsString(news));
                                } catch (JsonProcessingException ex) {
                                    log.error(ex.getMessage());
                                }
                            }else {
                                log.debug("获取到的资源值为空");
                            }
                        }
                    }else {
                        log.error("不能解析DataStreamId："+ds);
                    }
                }
                log.info("data receive: content" + obj.toString());
            }else {
                log.info("data receive: signature error");
            }
        }else {
            log.info("data receive: body empty error");
        }
        /*************明文模式  end****************/


        /********************************************************
         *  解析数据推送请求，加密模式
         *
         *  如果是加密模式使用以下代码
         ********************************************************/
        /*************加密模式  start****************/
//        var obj1 = com.sybd.znld.web.Util.resolveBody(body, true);
//        log.info("data receive:  body Object--- " +obj1);
//        if (obj1 != null){
//            var dataRight1 = com.sybd.znld.web.Util.checkSignature(obj1, token);
//            if (dataRight1){
//                var msg = com.sybd.znld.web.Util.decryptMsg(obj1, aeskey);
//                log.info("data receive: content" + msg);
//            }else {
//                log.info("data receive:  signature error " );
//            }
//        }else {
//            log.info("data receive: body empty error" );
//        }
        /*************加密模式  end****************/
        return "ok";
    }

    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     * @param msg 验证消息
     * @param nonce 随机串
     * @param signature 签名
     * @return msg值
     */
    @RequestMapping(value = "/receive", method = RequestMethod.GET)
    @ResponseBody
    public String check(@RequestParam(value = "msg") String msg,
                        @RequestParam(value = "nonce") String nonce,
                        @RequestParam(value = "signature") String signature) {

        log.info("url&token check: msg:{} nonce{} signature:{}",msg,nonce,signature);
        if (Util.checkToken(msg,nonce,signature,token)){
            return msg;
        }else {
            return "error";
        }
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        log.info("test");
        this.jmsTemplate.convertAndSend("mailbox", "hello");
        return "ok";
    }
}
