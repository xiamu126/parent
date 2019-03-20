package com.sybd.znld.service.model.user.dto;

import javax.validation.constraints.NotEmpty;

public class RegisterInput {
    @NotEmpty(message = "用户名不能为空")
    public String name;
    @NotEmpty(message = "密码不能为空")
    public String password;

    public RegisterInput(){}
    public RegisterInput(@NotEmpty(message = "用户名不能为空") String name, @NotEmpty(message = "密码不能为空") String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
