package com.htc.tool;

public class RedisKeyUtil {
    private static final String SPLIT = ":";  //key中的分隔符
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";   //被关注
    private static final String PREFIX_FOLLOWER = "follower";   //粉丝
    private static final String PREFIX_KAPTCHA = "kaptcha";     //验证码前缀
    private static final String PREFIX_TICKET = "ticket";       //登录凭证
    private static final String PREFIX_USER = "user";           //用户信息

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

    /**
     * 某个用户关注的实体（人、帖子）
     * 格式：followee:userId:entityType -> zset(entityId,time)
     */
    public static String getFolloweeKey(int userId, String entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    /**
     * 某实体的关注者
     * 格式：follower:entityType:entityId -> zset(userId,time)
     */
    public static String getFollowerKey(String entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    /**
     * 登录验证码对应的key
     *
     * @param owner 验证码临时的拥有者凭证
     */
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    /**
     * 登录凭证对应的key
     */
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    /**
     * 用户对应的key
     */
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }
}
