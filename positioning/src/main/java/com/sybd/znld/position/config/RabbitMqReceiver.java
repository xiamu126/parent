package com.sybd.znld.position.config;

import com.sybd.znld.mapper.lamp.GpggaMapper;
import com.sybd.znld.model.lamp.GpggaModel;
import com.sybd.znld.position.websocket.PositioningWebSocketHandler;
import com.sybd.znld.util.MyNumber;
import com.sybd.znld.util.MyString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMqReceiver {
    @Autowired
    private GpggaMapper gpggaMapper;

    //@RabbitListener(queues="#{anonymousQueue.name}")
    public void receive(Message msg){
        var lineTxt = new String(msg.getBody());
        try{
            var tmp = lineTxt.split(","); // 正常情况下，这里数组长度应该为15
            log.debug(tmp[2]+","+tmp[4]);
            var a = tmp[2].substring(0, 2);// 3109.5152754
            var b = tmp[2].substring(2);
            log.debug(a+","+b);
            var lat = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
            a = tmp[4].substring(0, 3);// 12038.8791150
            b = tmp[4].substring(3);
            log.debug(a+","+b);
            var lng = Double.parseDouble(a) + (Double.parseDouble(b) / 60);
            log.debug(lng+", "+lat);
            PositioningWebSocketHandler.sendAll(lng+","+lat);
            var props = msg.getMessageProperties();
            var filename = props.getHeaders().get("filename");
            if(!MyString.isEmptyOrNull(filename.toString())){
                var ret = this.gpggaMapper.selectByFilename(filename.toString());
                if(ret == null){
                    var model = new GpggaModel();
                    model.content = lineTxt + "\r\n";
                    model.filename = filename.toString();
                    this.gpggaMapper.insert(model);
                }else {
                    this.gpggaMapper.appendByFilename(filename.toString(), lineTxt + "\r\n");
                }
            }
        }catch (NumberFormatException | IndexOutOfBoundsException ex){
            log.debug("错误的数据："+lineTxt);
        }
    }
}
