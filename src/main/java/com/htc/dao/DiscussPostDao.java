package com.htc.dao;

import com.htc.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DiscussPostDao {
    /**
     *
     * @param userId 当用户id为0时表示所有用户
     * @param offset 分页用，起始行的行号，从0开始
     * @param rows 分页用，一页的行数
     */
    List<DiscussPost> selectDiscussPostByUserid(int userId,int offset,int rows);
    
    int selectDiscussPostRowsByUserid(int userId);
}
