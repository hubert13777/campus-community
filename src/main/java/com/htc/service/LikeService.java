package com.htc.service;

import com.htc.tool.HostHolder;
import com.htc.tool.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞，需要判断是取消赞还是赞
     *
     * @param entityUserId 被点赞的用户的id
     */
    public void Like(int userId, String entityType, int entityId, int entityUserId) {
        //加上事务保证原子性
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                //查询要放在事务之外
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);

                operations.multi();  //开始事务

                if (isMember) {   //已赞，需要删除赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);        //被点赞用户赞-1
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();   //结束事务并回传
            }
        });
    }

    /**
     * 查询点赞数量
     */
    public long getLikeCount(String entityType, int entityId) {
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeKey);
    }

    /**
     * 查询某人对某实体的点赞状态
     *
     * @return 1表示已赞，0表示未赞
     */
    public int getLikeStatus(int userId, String entityType, int entityId) {
        String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        boolean isMember = redisTemplate.opsForSet().isMember(likeKey, userId);
        if (isMember) {   //已赞
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * 查询某个用户获得的赞的数量
     */
    public int getUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return (count == null) ? 0 : count.intValue();
    }
}
