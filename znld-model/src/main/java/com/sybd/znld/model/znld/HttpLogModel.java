package com.sybd.znld.model.znld;

import com.sybd.znld.util.MyString;

import java.io.Serializable;
import java.time.Instant;
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
