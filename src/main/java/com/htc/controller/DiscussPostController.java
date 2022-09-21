package com.htc.controller;

import com.htc.annotation.LoginRequired;
import com.htc.entity.DiscussPost;
import com.htc.entity.User;
import com.htc.service.DiscussPostService;
import com.htc.service.UserService;
import com.htc.tool.CommunityUtil;
import com.htc.tool.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add")
    @ResponseBody
    @LoginRequired
    public String addDiscussPost(String title,String content){
        User user=hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403,"需要登录才可以发帖！");
        }
        DiscussPost post=new DiscussPost();
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

        return CommunityUtil.getJSONString(0,"发布成功!");
    }

    @GetMapping("/detail/{postId}")
    public String getDiscussPost(@PathVariable("postId") int postId, Model model){
        DiscussPost post=discussPostService.getPostByPostId(postId);
        User user = userService.getUserById(post.getUserId());

        model.addAttribute("post",post);
        model.addAttribute("user",user);

        //评论之后再添加

        return "/site/discuss-detail";
    }
}
