package com.htc.dao;

import com.htc.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostDao {
    /**
     * 置顶帖优先，新发布的优先，不获取拉黑状态的帖子
     * @param userId 当用户id为0时表示所有用户
     * @param offset 分页用，起始行的行号，从0开始
     * @param rows   分页用，一页的行数
     */
    List<DiscussPost> selectPostByUseridOrderByTime(@Param("userId") int userId, @Param("offset") int offset, @Param("rows") int rows);
    
    /**
     * 查找某个用户的发帖数
     * @param userId 用户id为0时表示查询所有用户的帖子
     * @return 该用户的帖子数量
     */
    int selectPostRowsByUserid(int userId);
    
    /**
     * 插入一条帖子
     * @return 返回1说明插入成功
     */
    int insertPost(DiscussPost discussPost);
}
