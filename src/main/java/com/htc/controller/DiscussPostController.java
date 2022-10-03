package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.Comment;
import com.htc.entity.DiscussPost;
import com.htc.entity.Page;
import com.htc.entity.User;
import com.htc.service.CommentService;
import com.htc.service.DiscussPostService;
import com.htc.service.UserService;
import com.htc.tool.CommunityConstant;
import com.htc.tool.CommunityUtil;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    @ResponseBody
    @LoginRequired
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "需要登录才可以发帖！");
        }
        DiscussPost post = new DiscussPost();
        //设置帖子属性
        post.setUserId(user.getUserId());
        post.setTitle(title);
        post.setContent(content);
        post.setType("0");                //普通
        post.setStatus("0");              //正常
        post.setCreateTime(new Date());
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setScore(0.0);
        //发布
        discussPostService.addDiscussPost(post);

        return CommunityUtil.getJSONString(0, "发布成功!");
    }

    @GetMapping("/detail/{postId}")
    public String getDiscussPost(@PathVariable("postId") int postId, Model model, Page page) {
        DiscussPost post = discussPostService.getPostByPostId(postId);
        User user = userService.getUserById(post.getUserId());

        model.addAttribute("post", post);
        model.addAttribute("user", user);

        //评论的分页信息
        page.setLimit(10);
        page.setPath("/discuss/detail/" + postId);
        page.setRows(post.getCommentCount());

        //获取帖子的评论
        List<Comment> commentList = commentService.getCommentsByEntity(CommunityConstant.ENTITY_TYPE_POST,
                post.getPostId(), page.getOffset(), page.getRows());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                //评论信息
                commentVo.put("comment", comment);
                //评论作者
                commentVo.put("user", userService.getUserById(comment.getUserId()));
                //获取评论的回复
                List<Comment> replyList = commentService.getCommentsByEntity(CommunityConstant.ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);   //不分页了
                //回复列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.getUserById(reply.getUserId()));
                        //处理回复的目标
                        User target = userService.getUserById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);
                //回复数量
                int replyCount = replyList.size();
                commentVo.put("replyCount", replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
