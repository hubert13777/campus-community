package com.htc.daoTest;

import com.htc.dao.DiscussPostDao;
import com.htc.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class PostDaoTest {
    @Autowired
    private DiscussPostDao discussPostDao;
    
    @Test
    public void insertPostTest(){
        DiscussPost post=new DiscussPost();
        post.setUserId(1);
        post.setTitle("章十六");
        post.setContent("致虚极，守静笃。万物并作，吾以观复。");
        post.setType("0");
        post.setStatus("0");
        post.setCreateTime(new Date());
        post.setCommentCount(0);
        post.setScore(32.45);
        System.out.println(post);
        discussPostDao.insertPost(post);
    }
    
    @Test
    public void selectPostTest(){
        List<DiscussPost> discussPosts = discussPostDao.selectPostByUseridOrderByTime(0, 0, 5);
        for(DiscussPost post:discussPosts){
            System.out.println(post);
        }
    }
    
    @Test
    public void selectPostRowsTest(){
        int res=discussPostDao.selectPostRowsByUserid(1);
        System.out.println(res);
    }
}
