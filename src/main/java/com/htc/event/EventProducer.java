package com.htc.event;

import com.alibaba.fastjson2.JSONObject;
import com.htc.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    /**
     * 处理事件
     */
    public void fireEvent(Event event) {
        //将时间发布到指定的topic
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
