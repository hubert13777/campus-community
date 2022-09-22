package com.htc.service;

import com.htc.dao.DiscussPostDao;
import com.htc.entity.DiscussPost;
import com.htc.tool.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * 获取若干满足条件的帖子
     *
     * @param userId 用户id，若为0表示所有用户
     * @param offset 分页用，偏移量
     * @param rows   分页，行数
     */
    public List<DiscussPost> getDiscussPosts(int userId, int offset, int rows) {
        return discussPostDao.selectPostByUseridOrderByTime(userId, offset, rows);
    }

    /**
     * @param userId 当为0时表示所有用户
     * @return 帖子的数量
     */
    public int getPostRows(int userId) {
        return discussPostDao.selectPostRowsByUserid(userId);
    }

    /**
     * 发布新帖子，已包含html标签、敏感字过滤
     *
     * @return 新帖子的postId
     */
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
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

    /**
     * 根据帖子的id返回帖子的完整信息
     */
    public DiscussPost getPostByPostId(int postId) {
        return discussPostDao.selectPostByPostId(postId);
    }

    /**
     * 修改帖子的评论数量
     */
    public int updateCommentCount(int postId,int commentCount){
        return discussPostDao.updateCommentCount(postId,commentCount);
    }
}
