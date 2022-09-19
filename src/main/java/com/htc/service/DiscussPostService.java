package com.htc.service;

import com.htc.dao.DiscussPostDao;
import com.htc.entity.DiscussPost;
import com.htc.tool.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private SensitiveFilter sensitiveFilter;
    
    public List<DiscussPost> getDiscussPosts(int userId,int offset,int rows){
        return discussPostDao.selectPostByUseridOrderByTime(userId,offset,rows);
    }

    /**
     * @param userId 当为0时表示所有用户
     * @return 帖子的数量
     */
    public int getPostRows(int userId){
        return discussPostDao.selectPostRowsByUserid(userId);
    }

    /**
     * 发布新帖子，已包含html标签、敏感字过滤
     * @return 1表示正常插入
     */
    public int addDiscussPost(DiscussPost post){
        if(post==null){
            throw new IllegalArgumentException("参数不能为空");
        }
        //去除HTML标签
        post.setTitle((HtmlUtils.htmlEscape(post.getTitle())));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        //过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostDao.insertPost(post);
    }
}
