package com.htc.controller;

import com.htc.entity.DiscussPost;
import com.htc.entity.User;
import com.htc.service.DiscussPostService;
import com.htc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
// @RequestMapping("/home")
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/index")
    public String getIndexPage(Model model){
        List<DiscussPost> allPosts = discussPostService.getDiscussPosts(0, 0, 10);
        List<Map<String,Object>> list=new ArrayList<>();
        if(allPosts!=null){
            for(DiscussPost post:allPosts){
                Map<String,Object> map=new HashMap<>();
                map.put("post",post);
                User user=userService.getUserById(post.getUserId());
                map.put("user",user);
                list.add(map);
            }
        }
        model.addAttribute("discussPosts",list);
        return "/index";
    }
}
