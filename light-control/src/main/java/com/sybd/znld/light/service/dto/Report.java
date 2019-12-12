package com.sybd.znld.light.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        public Integer online;
        public Integer fault;
        public Integer light;
    }
}
