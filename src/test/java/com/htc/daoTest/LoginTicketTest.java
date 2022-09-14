package com.htc.daoTest;

import com.htc.dao.LoginTicketDao;
import com.htc.entity.LoginTicket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class LoginTicketTest {
    @Autowired
    private LoginTicketDao loginTicketDao;

    @Test
    public void testInsertLoginTicket(){
        LoginTicket loginTicket=new LoginTicket();
        loginTicket.setUserId(1);
        loginTicket.setTicket("abcdefg");
        loginTicket.setStatus("0");
        loginTicket.setExpired(new Date(System.currentTimeMillis()+1000*60));
        loginTicketDao.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket(){
        LoginTicket loginTicket=loginTicketDao.selectByTicket("abcdefg");
        System.out.println(loginTicket);
        //修改状态
        loginTicketDao.updateStatus("abcdefg","1");
        //再次获取
        loginTicket=loginTicketDao.selectByTicket("abcdefg");
        System.out.println(loginTicket);
    }
}
