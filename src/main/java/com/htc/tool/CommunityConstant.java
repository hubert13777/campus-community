package com.htc.tool;

public interface CommunityConstant {
    /**
     * 激活成功状态
     */
    int ACTIVATION_SUCCESS = 0;

    /**
     * 重复激活
     */
    int ACTIVATION_REPEAT = 1;

    /**
     * 激活失败
     */
    int ACTIVATION_FAILURE = 2;

    /**
     * 默认的登录凭证有效时长，12h
     */
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    /**
     * 记住登录状态的登录有效时长，15天
     */
    int REMEMBER_EXPIRED_SECONDS = 3600 * 24 * 15;

    /**
     * 实体类型-帖子
     */
    String ENTITY_TYPE_POST = "1";

    /**
     * 实体类型-评论
     */
    String ENTITY_TYPE_COMMENT = "2";

    /**
     * 实体类型-用户
     */
    String ENTITY_TYPE_USER = "3";
}
