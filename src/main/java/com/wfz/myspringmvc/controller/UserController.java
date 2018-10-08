package com.wfz.myspringmvc.controller;

import com.wfz.myspringmvc.annotation.Controller;
import com.wfz.myspringmvc.annotation.Qualifier;
import com.wfz.myspringmvc.annotation.RequestMapping;
import com.wfz.myspringmvc.service.UserService;

@Controller("userController")
@RequestMapping("/user")
public class UserController {

    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping("/insert")
    public  void insert(){
        userService.insert();
    }


}
