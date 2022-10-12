package com.htc.event;

import com.alibaba.fastjson2.JSONObject;
import com.htc.entity.Event;
import com.htc.entity.Message;
import com.htc.service.MessageService;
import com.htc.tool.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {
    private static final Logger logger = LogManager.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USERID);   //发送者为系统用户
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setStatus("0");     //未读
        message.setCreateTime(new Date());

        Map<String ,Object> content=new HashMap<>();
        content.put("userId",event.getUserId());        //事件触发者
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());

        if(!event.getData().isEmpty()){
            for(Map.Entry<String,Object> entry:event.getData().entrySet()){
                content.put(entry.getKey(),entry.getValue());
            }
        }
        //map转为JSON格式
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}