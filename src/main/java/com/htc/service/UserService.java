package com.htc.service;

import com.htc.dao.UserDao;
import com.htc.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserDao userDao;
    
    public User getUserById(int userId){
        return userDao.selectUserById(userId);
    }
    
    public User getUserByUsername(String username){
        return userDao.selectUserByUsername(username);
    }
    
    public User getUserByEmail(String email){
        return userDao.selectUserByEmail(email);
    }
    
    public String getUsername(int userId){
        return userDao.selectUsernameByUserid(userId);
    }
}
