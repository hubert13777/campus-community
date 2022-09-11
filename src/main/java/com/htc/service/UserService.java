package com.htc.service;

import com.htc.dao.UserDao;
import com.htc.entity.User;
import com.htc.tool.CommunityConstant;
import com.htc.tool.CommunityUtil;
import com.htc.tool.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserDao userDao;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;
    
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

    /**
     * 注册一个新用户
     * @return 为空说明没有问题，否则map包含问题描述
     */
    public Map<String,Object> register(User user){
        Map<String, Object> map=new HashMap<>();
        // 处理空值
        if(user==null){
            throw new IllegalArgumentException("参数不能为null值!");
        }
        if(StringUtils.isBlank(user.getUsername())){
            map.put("username_msg","用户名不能为空!");
            return map;
        }else if(StringUtils.isBlank(user.getPassword())){
            map.put("password_msg","密码不能为空!");
            return map;
        }else if(StringUtils.isBlank(user.getEmail())){
            map.put("email_msg","邮箱不能为空!");
            return map;
        }

        // 验证账号是否可以被注册
        User u = userDao.selectUserByUsername(user.getUsername());
        if(u!=null){    //验证用户名是否已存在
            map.put("username_msg","该账号已存在!");
            return map;
        }
        u=userDao.selectUserByEmail(user.getEmail());
        if(u!=null){    //验证邮箱是否存在
            map.put("email_msg","该邮箱已被注册!!");
            return map;
        }

        // 正式注册
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));  //五位随机字符，作为salt值
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType("0");  //默认普通用户
        user.setStatus("0");  //默认正常状态
        user.setCode(CommunityUtil.generateUUID().substring(0,6));  //6位，充当激活码
        user.setHeadImageUrl(String.format("http://images.nowcoder.com/head/%dt.png",(int)(Math.random()*1000+1)));
        user.setCreateTime(new Date());
        userDao.insertUser(user);

        // 发送激活邮件
        Context context=new Context();
        context.setVariable("email",user.getEmail());
        //激活链接：http://localhost:8081/community/activation/13/code
        String url=domain+contextPath+"/activation/"+user.getUserId()+"/"+user.getCode();
        context.setVariable("url",url);
        String content=templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"仿牛客网-激活账号",content);

        return map;
    }

    /**
     * 注册时激活
     * @return 返回一个状态
     */
    public int activation(int userId,String code){
        User user=userDao.selectUserById(userId);
        if(user.getStatus()=="1"){    //已经激活过了
            return ACTIVATION_REPEAT;
        }else if(user.getCode().equals(code)){
            userDao.updateStatus(userId,"1");
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }
}
