package com.htc.dao;

import com.htc.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserDao {
    User selectUserById(int userId);
    
    User selectUserByUsername(String username);
    
    User selectUserByEmail(String email);
    
    @Select("select username from User where userId=#{userId}")
    String selectUsernameByUserid(int userId);
    
    int insertUser(User user);
    
    /**
     * 更新头像的url地址
     */
    int updateHeadImage(@Param("userId") int userId, @Param("url") String url);
    
    /**
     * 更新密码
     */
    int updatePassword(@Param("userId") int userId, @Param("password") String password);
}
