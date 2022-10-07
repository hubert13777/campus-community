package com.htc.tool;

public class RedisKeyUtil {
    private static final String SPLIT = ":";  //key中的分隔符
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";

    /**
     * 生成某个实体的赞的key
     * 格式：like:entity:[entityType]:[entityId] -> set(userId)
     */
    public static String getEntityLikeKey(String entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 某个用户获得的赞
     * 格式：like:user:[userId] -> int
     */
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }
}
