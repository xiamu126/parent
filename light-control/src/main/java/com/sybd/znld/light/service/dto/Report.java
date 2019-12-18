package com.sybd.znld.light.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sybd.znld.model.IEnum;

import java.util.List;

public class Report {
    @JsonProperty("organ_id")
    public String organId;
    @JsonProperty("electricity_summary")
    public Double electricitySummary;
    @JsonProperty("full_electricity_summary")
    public Double fullElectricitySummary;
    @JsonProperty("online_summary")
    public Double onlineSummary;
    @JsonProperty("fault_summary")
    public Double faultSummary;
    @JsonProperty("light_summary")
    public Double lightSummary;
    public List<Detail> details;

    public static class Detail {
        public String key;
        public Double electricity;
        @JsonProperty("full_electricity")
        public Double fullElectricity;
    }

    public enum TimeType implements IEnum {
        WEEK(0), MONTH(1), YEAR(2);
        TimeType(int v){
            this.value = v;
        }
        private int value;
        @Override
        public int getValue() {
            return this.value;
        }
    }
}
