package com.htc.entity;

import java.util.Date;

public class User {
    int userId;             //主键，用户id
    String username;        //用户名，最多20字
    String password;        //密码，经过加密的结果
    String salt;            //盐值，辅助加密
    String email;           //邮箱
    String type;            //0普通用户，1管理员，2版主
    String headImageUrl;    //头像图片地址
    Date createTime;        //注册时间
    
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", headImageUrl='" + headImageUrl + '\'' +
                ", createTime=" + createTime +
                '}';
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSalt() {
        return salt;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getHeadImageUrl() {
        return headImageUrl;
    }
    
    public void setHeadImageUrl(String headImageUrl) {
        this.headImageUrl = headImageUrl;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}