package com.hhz.bms.bms.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RocketMQMessageListener(topic = "warn-topic", consumerGroup = "warn-group")
public class WarnMessageConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("收到预警消息: " + message);
        // TODO: 可在这里进行进一步处理，比如存数据库、报警、记录日志等
    }
}

