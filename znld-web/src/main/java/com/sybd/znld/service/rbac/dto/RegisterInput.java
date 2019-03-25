package com.sybd.znld.service.rbac.dto;

import com.sybd.znld.util.MyString;
import com.sybd.znld.validate.Uuid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@ApiModel(value = "用户注册传入参数")
@AllArgsConstructor @NoArgsConstructor
public class RegisterInput implements Serializable {
    @ApiModelProperty(value = "用户名")
    @NotEmpty(message = "用户名不能为空")
    public String name;
    @ApiModelProperty(value = "用户密码")
    @NotEmpty(message = "密码不能为空")
    public String password;
    @ApiModelProperty(value = "所属组织")
    @Uuid(message = "所属组织不能为空")
    public String organizationId;

    public boolean isValid(){
        return !MyString.isAnyEmptyOrNull(name, password) && MyString.isUuid(organizationId);
    }
}
