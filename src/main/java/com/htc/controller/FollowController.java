package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.Page;
import com.htc.entity.User;
import com.htc.service.FollowService;
import com.htc.service.UserService;
import com.htc.tool.CommunityConstant;
import com.htc.tool.CommunityUtil;
import com.htc.tool.HostHolder;
import com.htc.tool.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //关注某实体
    @PostMapping(path = "/follow")
    @ResponseBody
    @LoginRequired
    public String follow(String entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getUserId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已关注!");
    }

    //取消关注
    @PostMapping(path = "/unfollow")
    @ResponseBody
    @LoginRequired
    public String unfollow(String entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getUserId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注!");
    }

    //查询某个用户的关注列表
    @GetMapping(path = "/followees/{userId}")
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        //分页设置
        page.setLimit(10);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.getFolloweeCount(userId, CommunityConstant.ENTITY_TYPE_USER));

        //查询
        List<Map<String, Object>> userList = followService.getFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getUserId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    //查询某个用户的粉丝列表
    @GetMapping(path = "/followers/{userId}")
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在");
        }
        model.addAttribute("user", user);

        //分页设置
        page.setLimit(10);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.getFollowerCount(CommunityConstant.ENTITY_TYPE_USER, userId));

        //查询
        List<Map<String,Object>> userList=followService.getFollowers(userId,page.getOffset(),page.getLimit());
        if(userList!=null){
            for(Map<String ,Object> map:userList){
                User u= (User) map.get("user");
                map.put("hasFollowed",hasFollowed(u.getUserId()));
            }
        }
        model.addAttribute("users",userList);

        return "/site/follower";
    }

    //工具方法，判断当前用户是否是粉丝
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) return false;
        return followService.hasFollowed(hostHolder.getUser().getUserId(), CommunityConstant.ENTITY_TYPE_USER, userId);
    }
}
