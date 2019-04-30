package com.sybd.znld.model.lamp;

import java.io.Serializable;
import java.time.LocalDateTime;

public class HttpLogModel implements Serializable {
    public String id;
    public String path;
    public String method;
    public String header;
    public String body;
    public String ip;
    public LocalDateTime triggerTime = LocalDateTime.now();
}
