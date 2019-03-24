package com.sybd.znld.controller;

import com.sybd.znld.service.OneNetConfigDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class DefaultPageController {
    private final OneNetConfigDeviceService onenetConfigDeviceService;

    @Autowired
    public DefaultPageController(OneNetConfigDeviceService onenetConfigDeviceService) {
        this.onenetConfigDeviceService = onenetConfigDeviceService;
    }

    /*@RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    public String index(Model model, HttpServletResponse response){
        var list = onenetConfigDeviceService.getDeviceIdNameMap();
        model.addAttribute("deviceIdNames", list);
        return "index";
    }*/

    @RequestMapping(value = {"/unauthorised"}, method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    public String unauthorised(){
        return "unauthorised";
    }

    @RequestMapping(value = {"/error"}, method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE})
    public String error(HttpServletRequest request, Principal principal, Authentication authentication){
        return "unauthorised";
    }

    @RequestMapping("/api/doc")
    public String greeting() {
        return "redirect:/swagger-ui.html";
    }
}
