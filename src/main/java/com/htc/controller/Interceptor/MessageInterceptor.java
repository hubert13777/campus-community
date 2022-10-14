package com.htc.controller.Interceptor;

import com.htc.entity.User;
import com.htc.service.MessageService;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int letterUnreadCount = messageService.getLetterUnreadCount(user.getUserId(), null);
            int noticeUnreadCount = messageService.getNoticeUnreadCount(user.getUserId(), null);
            modelAndView.addObject("messageUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }
}
