package com.hhz.bms.bms.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WarnMessageProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void sendWarningMessage(String topic, String msg) {
        try {
            rocketMQTemplate.convertAndSend(topic, msg);
            log.info("发送预警消息成功，Topic: {}, 内容: {}", topic, msg);
        } catch (Exception e) {
            log.error("发送预警消息失败", e);
        }
    }
}
