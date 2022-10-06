package com.htc.service;

import com.htc.tool.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞，需要判断是取消赞还是赞
     */
    public void Like(int userId, String entityType, int entityId) {
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        //判断是否已赞
        boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
        if(isMember){   //已赞，本次是取消赞
            redisTemplate.opsForSet().remove(likeKey,userId);
        }else{          //未点赞
            redisTemplate.opsForSet().add(likeKey,userId);
        }
    }

    /**
     * 查询点赞数量
     */
    public long getLikeCount(String entityType,int entityId){
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeKey);
    }

    /**
     * 查询某人对某实体的点赞状态
     * @return 1表示已赞，0表示未赞
     */
    public int getLikeStatus(int userId,String entityType,int entityId){
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        boolean isMember=redisTemplate.opsForSet().isMember(likeKey, userId);
        if(isMember){   //已赞
            return 1;
        }else{
            return 0;
        }
    }
}
