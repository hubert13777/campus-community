package com.htc.dao;

import com.htc.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDao {
    User selectUserById(int userId);
    
    User selectUserByUsername(String username);
    
    User selectUserByEmail(String email);
    
    int insertUser(User user);
    
    int updateHeadImage(@Param("userId") int userId, @Param("url") String url);
    
    int updatePassword(@Param("userId") int userId, @Param("password") String password);
}
