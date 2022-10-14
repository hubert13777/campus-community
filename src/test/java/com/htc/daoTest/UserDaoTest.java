package com.htc.daoTest;

import com.htc.dao.UserDao;
import com.htc.entity.User;
import com.htc.tool.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class UserDaoTest {
    @Autowired
    private UserDao userDao;
    
    @Test
    public void insertAdminTest(){
        User user=new User();
        user.setUserId(0);
        user.setUsername("系统");
        user.setPassword("iwgmhty4774747");
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType("1");      //管理员
        user.setStatus("1");    //直接激活
        // user.setEmail("666@qq.com");
        user.setHeadImageUrl(String.format("http://images.nowcoder.com/head/%dt.png", (int) (Math.random() * 1000 + 1)));
        user.setCreateTime(new Date());
        userDao.insertUser(user);
    }
    
    @Test
    public void selectTest(){
        // User user = userDao.selectUserById(1);
        // System.out.println(user);
        // user=userDao.selectUserByUsername("zhangsan");
        // System.out.println(user);
        // user=userDao.selectUserByEmail("666@qq.com");
        // System.out.println(user);
        String username=userDao.selectUsernameByUserid(1);
        System.out.println(username);
    }
    
    @Test
    public void updateHeadTest(){
        int res=userDao.updateHeadImage(1,"localhost");
        System.out.println(res);
    }
    
    @Test
    public void updatePasswordTest(){
        int res=userDao.updatePassword(1,"12345678");
        System.out.println(res);
    }
}
