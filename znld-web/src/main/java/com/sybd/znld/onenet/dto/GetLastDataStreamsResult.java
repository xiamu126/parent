package com.sybd.znld.onenet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class GetLastDataStreamsResult extends BaseResult {
    public boolean isOk(){
        return errno == 0 && data != null && data.devices != null && data.devices.length > 0;
    }
    private Date data;

    @Getter @Setter @ToString
    public static class Date{
        private Device[] devices;

        @Getter @Setter @ToString
        public static class Device{
            private String title;
            private String id;
            private DataStream[] datastreams;

            @Getter @Setter @ToString
            public static class DataStream {
                private String at;
                private String id;
                private Object value;
            }
        }
    }
}
