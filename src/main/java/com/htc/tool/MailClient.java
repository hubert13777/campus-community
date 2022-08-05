package com.htc.tool;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailClient {
    private static Logger logger= LogManager.getLogger(MailClient.class);
    
    @Autowired
    private JavaMailSender mailSender;
}
