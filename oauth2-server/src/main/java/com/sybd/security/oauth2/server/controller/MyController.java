package com.sybd.security.oauth2.server.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
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
