package com.paypal.dealbridge.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SampleController {
    
    @RequestMapping(path="/api/sample/hello_world", method=RequestMethod.GET)
    @ResponseBody
    public String helloWorld() {
        return "Hello World!";
    }
    
    @RequestMapping(path="/sample/greeting", method=RequestMethod.GET)
    public String greeting(@RequestParam("user") String user, Model model) {
    	model.addAttribute("user", user);
    	return "sample";
    }
    
}
