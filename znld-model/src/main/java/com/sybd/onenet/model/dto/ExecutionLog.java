package com.sybd.onenet.model.dto;

import com.sybd.onenet.model.OneNetKey;

import java.time.LocalDateTime;

public class ExecutionLog {
    public LocalDateTime time;
    public OneNetKey oneNetKey;
    public String cmd;
}
