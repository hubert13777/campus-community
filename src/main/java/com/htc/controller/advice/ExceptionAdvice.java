package com.htc.controller.advice;

import com.htc.tool.CommunityUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger= LogManager.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常: "+e.getMessage());
        for(StackTraceElement element:e.getStackTrace()){
            logger.error(element.toString());
        }
        //给浏览器响应
        String xRequestedWith = request.getHeader("x-requested-with");
        if(xRequestedWith.equals("XMLHttpRequest")){    //异步请求
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer=response.getWriter();
            writer.write(CommunityUtil.getJSONString(1,"服务器异常"));
        }else{                                          //请求的是网页
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}