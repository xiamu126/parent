package com.sybd.znld.model.rbac.dto;

import com.sybd.znld.model.lamp.LampModel;
import com.sybd.znld.model.rbac.UserModel;

import java.util.List;

public class InitAccountInput {
    public UserModel user;
    public String organizationName;
    public String oauth2ClientId;
    public String regionName;
    public List<LampModel> lamps;
}
