package com.htc.controller;

import com.htc.entity.DiscussPost;
import com.htc.entity.Page;
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

/**
 * 社区的主页
 */

@Controller
// @RequestMapping("/home")
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(Model model, Page page) {
        // 方法调用前，springMVC会自动实例化Model和Page，并将Page注入Model
        // thymeleaf可以直接访问Page对象中的数据
        page.setRows(discussPostService.getPostRows(0));
        page.setPath("/index");

        List<DiscussPost> allPosts = discussPostService.getDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String, Object>> res = new ArrayList<>();
        if (allPosts != null) {
            for (DiscussPost post : allPosts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.getUserById(post.getUserId());
                map.put("user", user);
                res.add(map);
            }
        }
        model.addAttribute("discussPosts", res);
        return "/index";
    }
}
