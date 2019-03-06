package com.sybd.rbac.model;

import java.time.LocalDateTime;

public class Organization {
    private Integer id;
    private String name;
    private String parentId;
    private String levelTree;
    private Integer positionOfTree;
    private String remark;
    private Integer lastOperatorId;
    private LocalDateTime lastOperateTime;
    private String lastOperateIp;
}
