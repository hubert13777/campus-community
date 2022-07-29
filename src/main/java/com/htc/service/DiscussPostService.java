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
    
    public int getPostRows(int userId){
        return discussPostDao.selectPostRowsByUserid(userId);
    }
}
