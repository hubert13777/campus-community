package com.htc.dao;

import com.htc.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageDao {
    /**
     * 查询指定用户的会话列表，针对每个会话只返回一条最新的私信
     */
    List<Message> selectMessageByUserid(int userId, int offset, int rows);

    /**
     * 查询指定用户的会话数量
     */
    int selectMessageCountByUserId(int userId);

    /**
     * 查询某个会话包含的私信列表
     */
    List<Message> selectLetters(String conversationId, int offset, int rows);

    /**
     * 查询某个会话包含的私信数量
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读消息数量
     * conversationId为空表明查询与所有用户的未读消息，否则查询与单个用户的未读数量
     */
    int selectLetterUnreadCount(int userId, String conversationId);

    /**
     * 添加一条私信消息
     */
    int insertMessage(Message message);

    /**
     * 改变消息已读/未读状态
     */
    int updateStatus(List<Integer> ids,String status);

    /**
     * 查询某一个主题（点赞、评论、关注）最新的通知
     */
    Message selectLatestNotice(int userId,String topic);

    /**
     * 查询某个主题包含的通知数量
     */
    int selectNoticeCount(int userId,String topic);


    /**
     * 查询某个主题中未读的通知的数量
     * 若topic为null则是查所有的未读数量
     */
    int selectNoticeUnreadCount(int userId,String topic);

    /**
     * 查询某个主题包含的所有通知
     */
    List<Message> selectNotices(int userId,String topic,int offset,int rows);
}
