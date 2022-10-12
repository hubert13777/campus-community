package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.Comment;
import com.htc.entity.DiscussPost;
import com.htc.entity.Event;
import com.htc.event.EventProducer;
import com.htc.service.CommentService;
import com.htc.service.DiscussPostService;
import com.htc.tool.CommunityConstant;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private DiscussPostService postService;

    @PostMapping("/add/{postId}")
    @LoginRequired
    public String addComment(@PathVariable("postId") int postId, Comment comment) {
        comment.setUserId(hostHolder.getUser().getUserId());
        comment.setStatus("0"); //状态有效
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        //触发评论事件
        Event event = new Event();
        event.setTopic(CommunityConstant.TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getUserId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", postId);
        //判断评论的目标是帖子还是评论
        if (comment.getEntityType().equals(CommunityConstant.ENTITY_TYPE_POST)) {
            DiscussPost target = postService.getPostByPostId(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType().equals(CommunityConstant.ENTITY_TYPE_COMMENT)) {
            Comment target = commentService.getCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.fireEvent(event);     //生产者发送事件

        return "redirect:/discuss/detail/" + postId;
    }
}
