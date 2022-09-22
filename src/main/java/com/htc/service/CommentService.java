package com.htc.service;

import com.htc.dao.CommentDao;
import com.htc.entity.Comment;
import com.htc.tool.CommunityConstant;
import com.htc.tool.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 返回指定实体的所有评论
     */
    public List<Comment> getCommentsByEntity(String entityType, int entityId, int offset, int rows) {
        return commentDao.selectCommentsByEntity(entityType, entityId, offset, rows);
    }

    /**
     * 返回评论数量
     */
    public int getCommentCount(String entityType, int entityId) {
        return commentDao.selectCountByEntity(entityType, entityId);
    }

    /**
     * 添加一个评论
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        //过滤HTML标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        int res = commentDao.insertComment(comment);

        //更新帖子的评论数量
        if (comment.getEntityType().equals(CommunityConstant.ENTITY_TYPE_POST)) {
            int count = commentDao.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }

        return res;
    }
}
