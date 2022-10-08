package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.User;
import com.htc.service.FollowService;
import com.htc.tool.CommunityConstant;
import com.htc.tool.CommunityUtil;
import com.htc.tool.HostHolder;
import com.htc.tool.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

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
}
