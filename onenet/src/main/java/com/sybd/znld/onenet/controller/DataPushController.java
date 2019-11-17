package com.sybd.znld.onenet.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.sybd.znld.mapper.lamp.DataDeviceOnOffMapper;
import com.sybd.znld.mapper.lamp.DataEnvironmentMapper;
import com.sybd.znld.mapper.lamp.OneNetResourceMapper;
import com.sybd.znld.model.environment.RawData;
import com.sybd.znld.model.environment.RealTimeData;
import com.sybd.znld.model.onenet.DataPushModel;
import com.sybd.znld.onenet.Util;
import com.sybd.znld.onenet.websocket.handler.AngleHandler;
import com.sybd.znld.onenet.websocket.handler.EnvironmentHandler;
import com.sybd.znld.onenet.controller.dto.News;
import com.sybd.znld.onenet.service.IOneNetService;
import com.sybd.znld.onenet.websocket.handler.OnOffHandler;
import com.sybd.znld.onenet.websocket.handler.PositionHandler;
import com.sybd.znld.util.MyDateTime;
import com.sybd.znld.util.MyNumber;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Controller
public class DataPushController {
    private final JmsTemplate jmsTemplate;
    private final OneNetResourceMapper oneNetResourceMapper;
    private final DataEnvironmentMapper dataEnvironmentMapper;
    private final DataDeviceOnOffMapper dataDeviceOnOffMapper;
    private final IOneNetService oneNetService;
    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private static String token ="abcdefghijkmlnopqrstuvwxyz";//用户自定义token和OneNet第三方平台配置里的token一致
    private static String aeskey ="whBx2ZwAU5LOHVimPj1MPx56QRe3OsGGWRe4dr17crV";//aeskey和OneNet第三方平台配置里的token一致

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public DataPushController(JmsTemplate jmsTemplate,
                              OneNetResourceMapper oneNetResourceMapper,
                              DataEnvironmentMapper dataEnvironmentMapper,
                              DataDeviceOnOffMapper dataDeviceOnOffMapper,
                              IOneNetService oneNetService, RedissonClient redissonClient, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.dataEnvironmentMapper = dataEnvironmentMapper;
        this.dataDeviceOnOffMapper = dataDeviceOnOffMapper;
        this.oneNetService = oneNetService;
        this.redissonClient = redissonClient;
        this.objectMapper = objectMapper;
    }

    private RawData extract(String body){
        String ds = null;
        try{
            ds = JsonPath.read(body,"$.msg.ds_id");
        }catch (Exception ignored){ }
        Object value = null;
        try{
            value = JsonPath.read(body,"$.msg.value");
        }catch (Exception ignored){ }
        LocalDateTime at = null;
        try{
            Long tmp = JsonPath.read(body,"$.msg.at");
            var zoneId = ZoneId.of("Asia/Shanghai");
            at = MyDateTime.toLocalDateTime(tmp, zoneId);
        }catch (Exception ignored){ }
        Integer deviceId = null;
        try{
            deviceId = JsonPath.read(body,"$.msg.dev_id");
        }catch (Exception ignored){ }
        String imei = null;
        try{
            imei = JsonPath.read(body,"$.msg.imei");
        }catch (Exception ignored){ }
        var rawData = new RawData();
        rawData.ds = ds;
        rawData.value = value;
        rawData.at = at;
        rawData.deviceId = deviceId;
        rawData.imei = imei;
        return rawData;
    }
    private void onOff(RawData rawData, String name) {
        var map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime."+rawData.deviceId);
        var realTimeData = new RealTimeData();
        realTimeData.describe = name;
        realTimeData.value = rawData.value;
        realTimeData.at = MyDateTime.toTimestamp(rawData.at);
        map.put(name, realTimeData); // 更新实时缓存

        var news = new News();
        news.deviceId = rawData.deviceId;
        news.name = name;
        news.value = rawData.value;
        news.at = MyDateTime.toTimestamp(rawData.at);
        try {
            OnOffHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
        } catch (JsonProcessingException ignored) { }
    }
    private void position(RawData rawData, String name) {
        var tmp = MyNumber.getDouble(rawData.value.toString());
        if(tmp != null && tmp != 0.0){
            var map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime."+rawData.deviceId);
            var realTimeData = new RealTimeData();
            realTimeData.describe = name;
            realTimeData.value = rawData.value;
            realTimeData.at = MyDateTime.toTimestamp(rawData.at);
            map.put(name, realTimeData); // 更新实时缓存
            // 经度和纬度是分开传过来的，所以再发送实时数据的时候，必须要保证经度和纬度的数据都存在时才发送
            // 判断经度和纬度的数据是否为同一批数据，看这两个数据的时差，如果是30秒内的，就是同一批数据
            Object lng = null;
            Object lat = null;
            LocalDateTime at = null;
            if(name.contains("经度")){
                // 是经度数据，那么就从缓存里查纬度数据
                realTimeData = (RealTimeData) map.get(name.replace("经", "纬"));
                if(realTimeData != null){
                    lng = rawData.value;
                    lat = realTimeData.value;
                    at = MyDateTime.toLocalDateTime(realTimeData.at);
                }
            }else {
                realTimeData = (RealTimeData) map.get(name.replace("纬", "经"));
                if(realTimeData != null){
                    lng = realTimeData.value;
                    lat = rawData.value;
                    at = MyDateTime.toLocalDateTime(realTimeData.at);
                }

            }
            if(at == null){
                return;
            }
            if(lng != null && lat != null){
                var seconds = Duration.between(at, rawData.at).getSeconds();
                if(Math.abs(seconds) <= 30){ // 是同一批的经纬度
                    try {
                        var news = new News();
                        news.deviceId = rawData.deviceId;
                        news.name = name;
                        news.value = lng+","+lat;
                        news.at = MyDateTime.toTimestamp(rawData.at);
                        PositionHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
                    } catch (JsonProcessingException ignored) { }
                }
            }
        }else{
            log.debug("经纬度不合法");
        }
    }
    private void angle(RawData rawData, String name) {
        var map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime."+rawData.deviceId);
        var realTimeData = new RealTimeData();
        realTimeData.describe = name;
        realTimeData.value = rawData.value;
        realTimeData.at = MyDateTime.toTimestamp(rawData.at);
        map.put(name, realTimeData); // 更新实时缓存

        var news = new News();
        news.deviceId = rawData.deviceId;
        news.name = name;
        news.value = rawData.value;
        news.at = MyDateTime.toTimestamp(rawData.at);
        try {
            AngleHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
        } catch (JsonProcessingException ignored) { }
    }
    private void environment(RawData rawData, String name) {
        var data = new DataPushModel();
        data.deviceId = rawData.deviceId;
        data.imei = rawData.imei;
        data.datastreamId = rawData.ds;
        data.name = name;
        data.value = rawData.value;
        data.at = rawData.at;
        this.dataEnvironmentMapper.insert(data); // // 环境数据需要保存入数据库

        var map = this.redissonClient.getMap("com.sybd.znld.onenet.realtime."+rawData.deviceId);
        var realTimeData = new RealTimeData();
        realTimeData.describe = name;
        realTimeData.value = rawData.value;
        realTimeData.at = MyDateTime.toTimestamp(rawData.at);
        map.put(name, realTimeData); // 更新实时缓存

        var news = new News();
        news.deviceId = rawData.deviceId;
        news.name = name;
        news.value = rawData.value;
        news.at = MyDateTime.toTimestamp(rawData.at);
        try {
            EnvironmentHandler.sendAll(this.objectMapper.writeValueAsString(news)); // 推送消息
        } catch (JsonProcessingException ignored) { }
    }

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
        // 明文模式
        var obj = Util.resolveBody(body, false); // 解析数据
        if(obj == null){
            log.debug("收到onenet推送的数据，但解析结果为空");
            return "";
        }
        log.debug("收到onenet推送的数据，解析结果为：" +obj);
        var dataRight = Util.checkSignature(obj, token);
        if(!dataRight){
            log.debug("对解析的数据做签名验证失败");
            return "";
        }
        var rawData = this.extract(body);
        if(rawData.ds != null){ // 为空时可能是其它数据，如登入
            var ids = rawData.ds.split("_");
            if(ids.length != 3){
                log.debug("不能解析DataStreamId："+rawData.ds);
                return "";
            }
            var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids[0], ids[1], ids[2]);
            if(name == null){ // 有些DataStreamId是没有定义的，如北斗定位的误差之类
                log.debug("通过DataStreamId获取相应的资源名称为空："+rawData.ds);
                return "";
            }
            if(rawData.value == null){
                log.debug("onenet推送过来的这个资源["+ name +"]的值为空");
                return "";
            }
            if(rawData.at == null){
                log.debug("onenet推送过来的这个资源["+ name +"]的更新时间为空");
                return "";
            }
            log.debug("onenet推送过来的这个资源["+ name +"]的值为"+rawData.value+"，更新时间为"+ MyDateTime.toString(rawData.at, MyDateTime.FORMAT1));
            if(rawData.deviceId == null){
                log.debug("onenet推送过来的这个资源["+ name +"]的deviceId为空");
                return "";
            }
            if(name.contains("开关")){
                this.onOff(rawData, name);
            } else if(name.contains("经度") || name.contains("纬度")) {
                this.position(rawData, name);
            } else if(name.contains("angle")){
                this.angle(rawData, name);
            } else if(name.contains("时间戳") ){
                log.debug("跳过时间戳");
            } else{
                this.environment(rawData, name);
            }
            return "ok";
        }
        // 加密模式
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
        //this.jmsTemplate.convertAndSend("mailbox", "hello");
        return "ok";
    }
}
