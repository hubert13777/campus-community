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
    public void insertTest(){
        User user=new User();
        user.setUsername("zhangsan");
        user.setPassword("88888888");
        user.setSalt("abcde");
        user.setEmail("666@qq.com");
        user.setType("0");
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
