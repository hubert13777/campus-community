package com.htc.service;

import com.htc.dao.MessageDao;
import com.htc.entity.Message;
import com.htc.tool.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private SensitiveFilter sensitiveFilter;

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

    /**
     * 添加一条私信记录
     * @return 1说明正常
     */
    public int addMessage(Message message){
        //过滤内容的敏感词
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        //添加私信记录
        return messageDao.insertMessage(message);
    }

    /**
     * 改变私信的未读/已读状态
     * @return 改变状态的私信条数
     */
    public int readMessage(List<Integer> ids){
        return messageDao.updateStatus(ids,"1");
    }
}