package com.sybd.znld.model.ministar;

import java.io.Serializable;
import java.time.LocalDateTime;

public class TwinkleBeautyGroupModel implements Serializable {
    public static class Status{
        public static final short UPLOADED = 0;
        public static final short RUNNING = 1;
        public static final short STOPPED = 2;
        public static final short STOPPED_MANUALLY = 3;
        public static final short NETWORK_ERROR = 4;
        public static final short UNKNOWN_ERROR = 5;
        public static boolean isValid(short v){
            switch (v){
                case UPLOADED:
                case RUNNING:
                case STOPPED:
                case STOPPED_MANUALLY:
                case NETWORK_ERROR:
                case UNKNOWN_ERROR:
                    return true;
                default:
                    return false;
            }
        }
    }
    public String id;
    public LocalDateTime beginTime;
    public LocalDateTime endTime;
    public Short status;
    public String regionId;
}
