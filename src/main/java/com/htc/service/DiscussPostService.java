package com.htc.service;

import com.htc.dao.DiscussPostDao;
import com.htc.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostDao discussPostDao;
    
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
}
