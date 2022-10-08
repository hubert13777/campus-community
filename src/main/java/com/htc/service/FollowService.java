package com.htc.service;

import com.htc.tool.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FollowService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 关注
     */
    public void follow(int userId, String entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();     //开启事务

                //值为关注的实体id
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                //值为粉丝的userId
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());

                return operations.exec();
            }
        });
    }


    /**
     * 取消关注
     */
    public void unfollow(int userId, String entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                operations.multi();     //开启事务

                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);

                return operations.exec();
            }
        });
    }

    /**
     * 查询关注的某类实体数量（关注了xx人/帖子）
     */
    public long getFolloweeCount(int userId, String entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);   //统计zset中元素数量
    }

    /**
     * 查询实体的粉丝数量
     */
    public long getFollowerCount(String entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否关注该实体
    public boolean hasFollowed(int userId, String entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        //查询结果不为null这说明该实体在关注列表中
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
}
