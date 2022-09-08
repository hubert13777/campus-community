package com.htc.toolTest;

import com.htc.tool.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
public class MailTest {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendMail(){
        String to="1207820441@qq.com";
        String subject="这是一封测试邮件";
        String content="1234567890<br>Hello World!<br>@#$%^&*()_+<br/><br/>到这里就结束了";
        mailClient.sendMail(to,subject,content);
    }

    @Test
    public void testHtmlMail(){
        String to="1207820441@qq.com";
        String subject="HTML测试";
        Context context=new Context();
        context.setVariable("username","帅逼");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail(to,subject,content);
    }
}
