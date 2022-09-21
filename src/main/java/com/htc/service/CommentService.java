package com.htc.service;

import com.htc.dao.CommentDao;
import com.htc.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

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
}
