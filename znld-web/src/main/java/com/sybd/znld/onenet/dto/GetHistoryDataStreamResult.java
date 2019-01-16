package com.sybd.znld.onenet.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class GetHistoryDataStreamResult extends BaseResult {

    public boolean isOk(){
        return errno == 0 && data != null && data.count > 0;
    }

    private Data data;

    @Getter @Setter @ToString
    public static class Data{
        private String cursor;
        private Integer count;
        private DataStream[] datastreams;

        @Getter @Setter @ToString
        public static class DataStream{
            private String id;
            private DataPoint[] datapoints;

            @Getter @Setter @ToString
            public static class DataPoint {
                private String at;
                private String value;
            }
        }
    }
}
