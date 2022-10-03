package com.htc.aspect;

import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {
    private static final Logger logger = LogManager.getLogger(ServiceLogAspect.class);

    @Pointcut("execution(* com.htc.service.*.*(..)")
    public void pointcut() {
    }

    @Before("pointcut()")
    public void before(JoinPoint jp) {
        //格式：用户[10.20.30.40]访问[包.类.方法] -- [时间]
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip=request.getRemoteHost();
        String time=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Signature signature = jp.getSignature();
        String methodPath=signature.getDeclaringTypeName()+"."+signature.getName();
        logger.info(String.format("用户[%s]访问[%s] -- [%s]。",ip,methodPath,time));
    }
}