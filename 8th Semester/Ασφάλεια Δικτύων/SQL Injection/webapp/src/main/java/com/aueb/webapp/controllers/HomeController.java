package com.aueb.webapp.controllers;

import com.aueb.webapp.WebappApplication;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@AllArgsConstructor
@RequestMapping("/home")
public class HomeController {

    @RequestMapping("/test1")
    @ResponseBody
    String sample() {
        return WebappApplication.loginMessage;
    }
}
