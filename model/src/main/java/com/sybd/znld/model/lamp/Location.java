package com.sybd.znld.model.lamp;

import com.sybd.znld.model.Status;

import java.util.UUID;

public class Location {
    public String id = UUID.randomUUID().toString().replace("-","");
    public String name;
    public Integer level;
    public Integer sequenceNumber;
    public String rootId;
    public String organizationId;
    public Status status = Status.OK;
}
