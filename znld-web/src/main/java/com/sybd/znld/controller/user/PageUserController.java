package com.sybd.znld.controller.user;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PageUserController {
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    public String login(){
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public String postLogin(Model model){//因为/api/login已经
        return "redirect:index"; //security会拦截
    }
}
