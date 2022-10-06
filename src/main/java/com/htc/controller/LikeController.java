package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.User;
import com.htc.service.LikeService;
import com.htc.tool.CommunityUtil;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping(path = "/like")
    @ResponseBody
    @LoginRequired
    public String like(String entityType, int entityId) {
        User user = hostHolder.getUser();
        //点赞或取消赞
        likeService.Like(user.getUserId(), entityType, entityId);
        //返回赞的数量
        Long likeCount = likeService.getLikeCount(entityType, entityId);
        //获取赞的状态
        int likeStatus = likeService.getLikeStatus(user.getUserId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0, null, map);   //0表示无异常
    }
}
