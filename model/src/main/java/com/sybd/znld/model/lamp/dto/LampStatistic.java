package com.sybd.znld.model.lamp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

public class LampStatistic {
    public MessageType type = MessageType.LAMP_STATISTIC;
    public Message message;
    public static class Message {
        public static boolean isVoltageError(double voltage) {
            if(voltage < 200) return true;
            if(voltage > 246) return true;
            return false;
        }
        public static boolean isElectricityError(double electricity) {
            if(electricity != 3.0) return true;
            return false;
        }
        public static boolean isRateError(double rate) {
            if(rate < 49.5) return true;
            if(rate > 50.5) return true;
            return false;
        }
        public String id; // 路灯编号
        public String imei;
        @JsonProperty("device_id")
        public Integer deviceId;
        public ValueError<Double> voltage; // 电压
        public ValueError<Double> electricity; // 电流
        public ValueError<Double> power; // 功率
        @JsonProperty("power_factor")
        public ValueError<Double> powerFactor; // 功率因数
        public ValueError<Double> energy; // 电能
        @JsonProperty("total_energy")
        public Double totalEnergy; // 总的累计电能
        public ValueError<Double> rate; // 频率
        public ValueError<Integer> brightness; // 亮度
        @JsonProperty("update_time")
        public Long updateTime;

        @JsonProperty("inner_temperature")
        public Double innerTemperature; // 温度
        @JsonProperty("inner_humidity")
        public Double innerHumidity; // 湿度
        @JsonProperty("angle_x")
        public Double angleX; // X倾斜角度
        @JsonProperty("angle_y")
        public Double angleY; // Y倾斜角度
        public List<Boolean> isSwitchOn; // 继电器开关，0代表关，1代表关
        public Double temperature; // 大气温度
        public Double humidity; // 大气湿度
        public Double pm25;
        public Double pm10;
        public Double co;
        public Double no2;
        public Double so2;
        public Double o3;
        public Double lat; // 纬度
        public Double lon; // 经度
        public Double hgt; // 高度
        public Double spd; // 速度
        public Integer stn; // 卫星数量
        public Double hddp; // 水平位置相对精度

        @NoArgsConstructor @AllArgsConstructor
        public static class ValueError <T> {
            public T value;
            @JsonProperty("is_error")
            public Boolean isError;
        }
    }
}
