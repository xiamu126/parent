package com.sybd.security.oauth2.server.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class UserEntity implements Serializable {
    @TableId(value = "id", type = IdType.UUID)
    private String id;
    @TableField("name")
    private String name;
    @TableField("password")
    private String password;
    @TableField("phone")
    private String phone;
    @TableField("email")
    private String email;
    @TableField("gender")
    private Short gender;
    @TableField("age")
    private Short age;
    @TableField("contactAddress")
    private String contactAddress;
    @TableField("realName")
    private String realName;
    @TableField("idCardNo")
    private String idCardNo;
    @TableField("authorities")
    private String authorities;
}
