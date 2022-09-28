package com.htc.service;

import com.htc.dao.MessageDao;
import com.htc.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;

    /**
     * 获取某个用户的私信列表，只返回每个会话最新的消息
     */
    public List<Message> getConversations(int userId, int offset, int rows) {
        return messageDao.selectMessageByUserid(userId, offset, rows);
    }

    /**
     * 返回某个用户的会话数量
     */
    public int getConversationCount(int userId) {
        return messageDao.selectMessageCountByUserId(userId);
    }

    /**
     * 返回某个会话的所有消息
     */
    public List<Message> getLetters(String conversationId, int offset, int rows) {
        return messageDao.selectLetters(conversationId, offset, rows);
    }

    /**
     * 查询某个会话包含的私信数量
     */
    public int getLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    /**
     * 查询与某个用户（或所有用户）未读消息数量
     * conversationId为空表明查询与所有用户的未读消息，否则查询与单个用户的未读数量
     */
    public int getLetterUnreadCount(int userId, String conversationId) {
        return messageDao.selectLetterUnreadCount(userId, conversationId);
    }
}