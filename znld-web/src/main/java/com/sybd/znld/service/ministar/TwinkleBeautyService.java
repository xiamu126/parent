package com.sybd.znld.service.ministar;

import com.sybd.znld.db.DbSource;
import org.springframework.stereotype.Service;

@Service
@DbSource("ministar")
public class TwinkleBeautyService implements ITwinkleBeautyService {
    public static class Status{
        public static final short UPLOADED = 3;
        public static final short RUNNING = 4;
        public static final short STOPPED = 5;
        public static final short UNKNOWN = 6;

        public static boolean isValid(short v){
            switch (v){
                case UPLOADED:
                case RUNNING:
                case STOPPED:
                case UNKNOWN:
                    return true;
                default:
                    return false;
            }
        }
    }
}
