package com.htc.controller;

import com.htc.entity.User;
import com.htc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        Map<String, Object> map = userService.register(user);
        if (map == null || map.isEmpty()) {   //注册成功
            model.addAttribute("msg", "注册成功，已向您的邮箱发送激活邮件，请尽快激活！");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("username_msg", map.get("username_msg"));
            model.addAttribute("password_msg", map.get("password_msg"));
            model.addAttribute("email_msg", map.get("email_msg"));
            return "/site/register";
        }
    }

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result=userService.activation(userId,code);
        if(result==UserService.ACTIVATION_SUCCESS){     //成功
            model.addAttribute("msg","激活成功！");
            model.addAttribute("target","/site/login");
        }else if(result==UserService.ACTIVATION_REPEAT){    //重复
            model.addAttribute("msg","无效操作，该账号已经激活过");
            model.addAttribute("target","/index");
        }else{  //激活失败
            model.addAttribute("msg","激活失败，请检查您的激活码是否正确");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
}
