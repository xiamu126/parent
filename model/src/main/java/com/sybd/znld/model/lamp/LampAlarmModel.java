package com.sybd.znld.model.lamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IEnum;
import com.sybd.znld.model.MyEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public class LampAlarmModel {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String lampId;
    public String lampName;
    public String content;
    public LocalDateTime at;
    public Status status;
    public AlarmType type;
    public String regionId;
    public String regionName;
    public String organId;

    @MyEnum
    public enum Status implements IEnum {
        UNCONFIRMED(0), CONFIRMED(1), HANDLED(2), IGNORED(3)
        ;
        Status(int v) {
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }

        public String getDescribe() {
            switch (this.value) {
                case 0 : return "未确认";
                case 1 : return "已确认";
                case 2 : return "已处理";
                case 3 : return "已忽略";
                default:
                    return "";
            }
        }
    }

    @MyEnum
    public enum AlarmType implements IEnum {
        COMMON(0)
        ;
        AlarmType(int v) {
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }

        public String getDescribe() {
            switch (this.value) {
                case 0 : return "一般故障";
                default:
                    return "";
            }
        }
    }
}
