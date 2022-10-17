package com.htc.service;

import com.htc.dao.LoginTicketDao;
import com.htc.dao.UserDao;
import com.htc.entity.LoginTicket;
import com.htc.entity.User;
import com.htc.tool.CommunityConstant;
import com.htc.tool.CommunityUtil;
import com.htc.tool.MailClient;
import com.htc.tool.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserDao userDao;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    // @Autowired
    // private LoginTicketDao loginTicketDao;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User getUserById(int userId) {
        // return userDao.selectUserById(userId);
        //redis优化性能
        User user=getCache(userId);
        if(user==null){
            initCache(userId);
        }
        return user;
    }

    public User getUserByUsername(String username) {
        return userDao.selectUserByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userDao.selectUserByEmail(email);
    }

    public String getUsername(int userId) {
        return userDao.selectUsernameByUserid(userId);
    }

    /**
     * 注册一个新用户
     *
     * @return 为空说明没有问题，否则map包含问题描述
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();
        // 处理空值
        if (user == null) {
            throw new IllegalArgumentException("参数不能为null值!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("username_msg", "用户名不能为空!");
            return map;
        } else if (StringUtils.isBlank(user.getPassword())) {
            map.put("password_msg", "密码不能为空!");
            return map;
        } else if (StringUtils.isBlank(user.getEmail())) {
            map.put("email_msg", "邮箱不能为空!");
            return map;
        }

        // 验证账号是否可以被注册
        User u = userDao.selectUserByUsername(user.getUsername());
        if (u != null) {    //验证用户名是否已存在
            map.put("username_msg", "该账号已存在!");
            return map;
        }
        u = userDao.selectUserByEmail(user.getEmail());
        if (u != null) {    //验证邮箱是否存在
            map.put("email_msg", "该邮箱已被注册!!");
            return map;
        }

        // 正式注册
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));  //五位随机字符，作为salt值
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType("0");  //默认普通用户
        user.setStatus("0");  //默认正常状态
        user.setCode(CommunityUtil.generateUUID().substring(0, 6));  //6位，充当激活码
        user.setHeadImageUrl(String.format("http://images.nowcoder.com/head/%dt.png", (int) (Math.random() * 1000 + 1)));
        user.setCreateTime(new Date());
        userDao.insertUser(user);

        // 发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //激活链接：http://localhost:8081/community/activation/13/code
        String url = domain + contextPath + "/activation/" + user.getUserId() + "/" + user.getCode();
        context.setVariable("url", url);
        String content = templateEngine.process("mail/activation", context);
        mailClient.sendMail(user.getEmail(), "仿牛客网-激活账号", content);

        return map;
    }

    /**
     * 注册时激活
     *
     * @return 返回一个状态
     */
    public int activation(int userId, String code) {
        User user = userDao.selectUserById(userId);
        if (user.getStatus().equals("1")) {    //已经激活过了
            return ACTIVATION_REPEAT;
        } else if (user.getCode().equals(code)) {
            userDao.updateStatus(userId, "1");
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    /**
     * 用户登录时使用
     *
     * @param username       用户名
     * @param password       输入的密码，未加密
     * @param expiredSeconds 登录有效时长，单位为秒
     * @return 一个包含问题描述的map，若未出错则返回ticket内容
     */
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();
        //检查参数是否为空值
        if (StringUtils.isBlank(username)) {
            map.put("username_msg", "账号不能为空!");
            return map;
        } else if (StringUtils.isBlank(password)) {
            map.put("password_msg", "密码不能为空!");
            return map;
        }
        //检查账号合法性
        User user = userDao.selectUserByUsername(username);
        if (user == null) {
            map.put("username_msg", "该账号存在!");
            return map;
        } else if (user.getStatus().equals("0")) {    //未激活账号
            map.put("username_msg", "账号未激活!");
            return map;
        } else {      //检查密码是否一致
            String res = CommunityUtil.md5(password + user.getSalt());
            if (!res.equals(user.getPassword())) {
                map.put("password_msg", "密码错误!");
                return map;
            }
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getUserId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus("0");
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * expiredSeconds));

        //插入记录
        //存到redis中
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey, loginTicket);  //会自动序列化

        //正常则返回ticket的值作为登录参考
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录
     */
    public void logout(String ticket) {
        // loginTicketDao.updateStatus(ticket, "1");
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        //该登录凭证改为无效状态
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus("1");
        redisTemplate.opsForValue().set(redisKey, loginTicket);  //再次存入
    }

    /**
     * 获取用户登录ticket
     */
    public LoginTicket getLoginTicket(String ticket) {
        // return loginTicketDao.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 更新头像图片路径
     *
     * @return 1表示成功
     */
    public int updateHeader(int userId, String url) {
        // return userDao.updateHeadImage(userId, url);
        int rows=userDao.updateHeadImage(userId, url);  //记录更新的行数
        clearCache(userId);     //清理缓存
        return rows;
    }

    //1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(redisKey);
    }

    //2.缓存中没有则初始化缓存数据
    private User initCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        User user = userDao.selectUserById(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //3.数据变更时，清除缓存
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }
}
