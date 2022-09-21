package com.htc.dao;

import com.htc.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentDao {
    /**
     * 获取某一个实体的评论，带分页
     */
    List<Comment> selectCommentsByEntity(String entityType,int entityId,int offset,int rows);

    /**
     * 返回评论数量
     */
    int selectCountByEntity(String entityType,int entityId);
}
