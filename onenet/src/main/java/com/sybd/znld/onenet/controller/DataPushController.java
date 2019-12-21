package com.sybd.znld.onenet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sybd.znld.mapper.lamp.*;
import com.sybd.znld.onenet.Util;
import com.sybd.znld.onenet.service.IMessageService;
import com.sybd.znld.service.onenet.IOneNetService;
import com.sybd.znld.util.MyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Controller
public class DataPushController {
    private final OneNetResourceMapper oneNetResourceMapper;
    private final RabbitTemplate rabbitTemplate;
    private final IMessageService messageService;

    private static String token = "sybd";//用户自定义token和OneNet第三方平台配置里的token一致
    private static String aeskey = "ZHKi9o3I9ot0v1rcwCcffgZdyEMa3db45j1r6Pt3bNC";//aeskey和OneNet第三方平台配置里的token一致

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public DataPushController(OneNetResourceMapper oneNetResourceMapper,
                              RabbitTemplate rabbitTemplate,
                              IMessageService messageService) {
        this.oneNetResourceMapper = oneNetResourceMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.messageService = messageService;
    }

    private void processMsg(String msg) {
        var rawData = this.messageService.extractUpMsg(msg);
        var type = this.messageService.getUpMsgType(msg);
        if (type != null) {
            if (type == 2) {
                log.debug("设备上下线消息");
                this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_UP_MSG_ONLINE_ROUTING_KEY, msg);
            } else if (type == 1) {
                //log.debug("设备上传数据点消息");
            } else if (type == 7) {
                //log.debug("缓存命令下发后结果上报");
            } else {
                log.debug("未知的信息类型");
            }
        }
        if (rawData.ds != null) { // 为空时可能是其它数据，如登入
            var ids = rawData.getIds();
            if (ids == null || ids.isEmpty()) {
                log.debug("不能解析DataStreamId：" + rawData.ds);
                return;
            }
            var name = this.oneNetResourceMapper.selectNameByDataStreamId(ids.get(0), ids.get(1), ids.get(2));
            if (name == null) { // 有些DataStreamId是没有定义的，如北斗定位的误差之类
                log.debug("通过DataStreamId获取相应的资源名称为空：" + rawData.ds);
                return;
            }
            if (rawData.value == null) {
                log.debug("onenet推送过来的这个资源[" + name + "]的值为空");
                return;
            }
            if (rawData.at == null) {
                log.debug("onenet推送过来的这个资源[" + name + "]的更新时间为空");
                return;
            }
            log.debug("onenet推送过来的这个资源[" + name + "]的值为" + rawData.value + "，更新时间为" + MyDateTime.toString(rawData.at, MyDateTime.FORMAT1));
            if (rawData.deviceId == null) {
                log.debug("onenet推送过来的这个资源[" + name + "]的deviceId为空");
                return;
            }
            if (name.contains("开关")) {
                this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_UP_MSG_ONOFF_ROUTING_KEY, msg);
            } else if (name.contains("经度") || name.contains("纬度")) {
                this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_UP_MSG_POSITION_ROUTING_KEY, msg);
            } else if (name.contains("angle")) {
                this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_UP_MSG_ANGLE_ROUTING_KEY, msg);
            } else if (name.contains("时间戳")) {
                log.debug("跳过时间戳");
            } else if (name.contains("单灯")) {
                this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_UP_MSG_LIGHT_ROUTING_KEY, msg);
            } else {
                this.rabbitTemplate.convertAndSend(IOneNetService.ONENET_TOPIC_EXCHANGE, IOneNetService.ONENET_UP_MSG_ENVIRONMENT_ROUTING_KEY, msg);
            }
        }
    }

    /**
     * 功能描述：第三方平台数据接收。<p>
     * <ul>注:
     *     <li>1.OneNet平台为了保证数据不丢失，有重发机制，如果重复数据对业务有影响，数据接收端需要对重复数据进行排除重复处理。</li>
     *     <li>2.OneNet每一次post数据请求后，等待客户端的响应都设有时限，在规定时限内没有收到响应会认为发送失败。
     *          接收程序接收到数据时，尽量先缓存起来，再做业务逻辑处理。</li>
     * </ul>
     *
     * @param body 数据消息
     * @return 任意字符串。OneNet平台接收到http 200的响应，才会认为数据推送成功，否则会重发。
     */
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @ResponseBody
    public String receive(@RequestBody String body) {
        // 加密模式
        try {
            var obj = Util.resolveBody(body, true);
            //log.info("data receive:  body Object--- " +obj);
            if (obj != null){
                var dataRight = Util.checkSignature(obj, token);
                if (dataRight){
                    var msg = Util.decryptMsg(obj, aeskey);
                    this.processMsg(msg);
                    log.info("data receive: content" + msg);
                }else {
                    log.info("data receive:  signature error " );
                }
            }else {
                log.info("data receive: body empty error" );
            }
        }catch (Exception ex) {
            log.error(ex.getMessage());
            log.error(ExceptionUtils.getStackTrace(ex));
        }
        return "ok";
    }

    /**
     * 功能说明： URL&Token验证接口。如果验证成功返回msg的值，否则返回其他值。
     *
     * @param msg       验证消息
     * @param nonce     随机串
     * @param signature 签名
     * @return msg值
     */
    @RequestMapping(value = "/receive", method = RequestMethod.GET)
    @ResponseBody
    public String check(@RequestParam(value = "msg") String msg,
                        @RequestParam(value = "nonce") String nonce,
                        @RequestParam(value = "signature") String signature) {

        log.info("url&token check: msg:{} nonce{} signature:{}", msg, nonce, signature);
        if (Util.checkToken(msg, nonce, signature, token)) {
            return msg;
        } else {
            return "error";
        }
    }
}
