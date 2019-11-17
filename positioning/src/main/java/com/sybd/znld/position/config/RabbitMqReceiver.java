package com.sybd.znld.position.config;

import com.jayway.jsonpath.JsonPath;
import com.sybd.znld.mapper.lamp.GpggaMapper;
import com.sybd.znld.model.lamp.GpggaModel;
import com.sybd.znld.position.model.Point;
import com.sybd.znld.position.websocket.handler.RealTimeHandler;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class RabbitMqReceiver {
    @Autowired
    private GpggaMapper gpggaMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RestTemplate restTemplate;

    // $GPGGA,014122.00,3108.8641303,N,12039.0044451,E,1,07,3.6,9.6985,M,8.072,M,,*64
    public static Point extract(String line){
        try{
            var tmp = line.split(","); // 正常情况下，这里数组长度应该为15
            var a = tmp[2].substring(0, 2);// 3109.5152754
            var b = tmp[2].substring(2);
            var lat = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
            a = tmp[4].substring(0, 3);// 12038.8791150
            b = tmp[4].substring(3);
            var lng = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
            var point = new Point();
            point.lng = lng;
            point.lat = lat;
            return point;
        }catch (NumberFormatException | IndexOutOfBoundsException ex){
            log.debug("错误的数据："+line);
        }
        return null;
    }

    public static Point toBaiduCoordinate(RestTemplate restTemplate, Point point){
        if(restTemplate == null || point == null){
            return null;
        }
        var builder = UriComponentsBuilder
                .fromHttpUrl("http://api.map.baidu.com/geoconv/v1/")
                .queryParam("coords", point.lng+","+point.lat)
                .queryParam("from", 1)
                .queryParam("to", 5)
                .queryParam("ak", "x1ySji2AxajkmThvd8weGwOQ");
        var converted = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, Object.class);
        var body = converted.getBody();
        if(body != null){
            int status = JsonPath.read(body, "$.status");
            if(status == 0){
                var tmp = new Point();
                tmp.lng = JsonPath.read(body, "$.result[0].x");
                tmp.lat = JsonPath.read(body, "$.result[0].y");
                return tmp;
            }
        }
        return null;
    }

    @RabbitListener(queues="#{anonymousQueue.name}")
    public void receive(Message msg){
        var lineTxt = new String(msg.getBody());
        var lngLat = extract(lineTxt);
        if(lngLat == null){
            return;
        }
        // 转换为百度坐标
        var convertedPoint = toBaiduCoordinate(this.restTemplate, lngLat);
        RealTimeHandler.sendAll(convertedPoint.toString());
        var props = msg.getMessageProperties();
        var filename = props.getHeaders().get("filename");
        if(!MyString.isEmptyOrNull(filename.toString())){
            // 这里存在一个问题，如果本模块打开了多个实例，每个实例都会接收到这条信息，相同filename，null判断都为真，从而连续insert两次
            // 分布式锁
            var locker = this.redissonClient.getLock(this.getClass().getName()+"#gpggaMapper.insert");
            if(locker.tryLock()){
                try{
                    locker.lock();
                    var ret = this.gpggaMapper.selectByFilename(filename.toString());
                    if(ret == null){
                        var model = new GpggaModel();
                        model.content = convertedPoint.toString();
                        model.filename = filename.toString();
                        this.gpggaMapper.insert(model);
                    }else {
                        this.gpggaMapper.appendByFilename(filename.toString(), ";"+convertedPoint.toString());
                    }
                }finally {
                    locker.forceUnlock();
                }
            }else{
                log.debug("获取锁失败");
            }
        }
    }
}
