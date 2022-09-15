package com.htc.controller.Interceptor;

import com.htc.entity.LoginTicket;
import com.htc.entity.User;
import com.htc.service.UserService;
import com.htc.tool.CookieUtil;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //Controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的凭证，可能不存在
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            LoginTicket loginTicket = userService.getLoginTicket(ticket);
            //检查凭证是否有效
            if (loginTicket != null && loginTicket.getStatus().equals("0") && loginTicket.getExpired().after(new Date())) {
                User user = userService.getUserById(loginTicket.getUserId());
                //在本次请求中持有该用户信息
                hostHolder.setUser(user);
            }
        }

        return true;
    }

    //Controller之后，模板之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    //模板引擎之后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
