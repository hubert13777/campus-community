package com.htc.controller;

import com.google.code.kaptcha.Producer;
import com.htc.entity.User;
import com.htc.service.UserService;
import com.htc.tool.CommunityConstant;
import com.htc.tool.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController {
    private static Logger logger = LogManager.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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

    //激活用的页面
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        if (result == UserService.ACTIVATION_SUCCESS) {     //成功
            model.addAttribute("msg", "激活成功！");
            model.addAttribute("target", "/site/login");
        } else if (result == UserService.ACTIVATION_REPEAT) {    //重复
            model.addAttribute("msg", "无效操作，该账号已经激活过");
            model.addAttribute("target", "/index");
        } else {  //激活失败
            model.addAttribute("msg", "激活失败，请检查您的激活码是否正确");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        //验证码存入session
        session.setAttribute("kaptcha", text);
        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            ImageIO.write(image, "png", outputStream);
        } catch (IOException e) {
            logger.error("响应验证码请求失败:" + e.getMessage());
        }
    }

    /**
     * 登录页面提交信息后检查并处理
     * @param code     验证码
     * @param remember true表示勾选记住我
     */
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean remember,
                        Model model, HttpSession session, HttpServletResponse response) {
        //检查验证按是否正确
        String kaptcha = (String) session.getAttribute("kaptcha");
        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("code_msg", "验证码不正确!");
            return "/site/login";
        }

        //检查账号、密码
        int expiredSeconds = remember ? CommunityConstant.REMEMBER_EXPIRED_SECONDS:CommunityConstant.DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        if(map.containsKey("ticket")){      //成功
            Cookie cookie=new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            return "redirect:/index";
        }else{      //失败
            model.addAttribute("username_msg",map.get("username_msg"));
            model.addAttribute("password_msg",map.get("password_msg"));

            return "/site/login";
        }
    }

    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        return "redirect:/login";
        // return "redirect:/index";
    }
}
