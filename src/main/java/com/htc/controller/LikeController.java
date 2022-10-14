package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.Event;
import com.htc.entity.User;
import com.htc.event.EventProducer;
import com.htc.service.LikeService;
import com.htc.tool.CommunityConstant;
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

    @Autowired
    private EventProducer eventProducer;

    @PostMapping(path = "/like")
    @ResponseBody
    @LoginRequired
    public String like(String entityType, int entityId,int entityUserId,int postId) {
        User user = hostHolder.getUser();
        //点赞或取消赞
        likeService.Like(user.getUserId(), entityType, entityId,entityUserId);
        //返回赞的数量
        Long likeCount = likeService.getLikeCount(entityType, entityId);
        //获取赞的状态
        int likeStatus = likeService.getLikeStatus(user.getUserId(), entityType, entityId);

        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        //若点赞状态为1，触发点赞事件
        if(likeStatus==1){
            Event event=new Event();
            event.setTopic(CommunityConstant.TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getUserId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJSONString(0, null, map);   //0表示无异常
    }
}