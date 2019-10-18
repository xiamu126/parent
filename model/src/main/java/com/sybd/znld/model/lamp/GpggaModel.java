package com.sybd.znld.model.lamp;

import java.time.LocalDateTime;
import java.util.UUID;

public class GpggaModel {
    public String id = UUID.randomUUID().toString().replace("-","");
    public LocalDateTime beginTime;
    public LocalDateTime endTime;
    public String content;
    public String filename;
}
