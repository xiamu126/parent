package com.sybd.znld.oauth2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class MyController {
    @GetMapping("/auth")
    public String login() {
        return "login";
    }

    @GetMapping({"/success", "/"})
    public String success() {
        return "success";
    }

    @GetMapping("/error")
    public String error(){
        return "error";
    }
}
