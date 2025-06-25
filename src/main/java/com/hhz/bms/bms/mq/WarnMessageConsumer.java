package com.hhz.bms.bms.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhz.bms.bms.dto.WarnReportDTO;
import com.hhz.bms.bms.service.WarnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RocketMQMessageListener(topic = "warn-topic", consumerGroup = "warn-group")
public class WarnMessageConsumer implements RocketMQListener<String> {

    @Autowired
    private ObjectMapper objectMapper; // Jackson ObjectMapper 用于转换 JSON

    @Autowired
    private WarnService warnService;

    @Override
    public void onMessage(String message) {
        try {
            // 打印接收到的原始消息
            log.info("收到预警消息: " + message);

            // 将消息字符串转换为 List<WarnReportDTO> 类型
            List<WarnReportDTO> warnList = objectMapper.readValue(message, objectMapper.getTypeFactory().constructCollectionType(List.class, WarnReportDTO.class));
            log.info("转换后的数据: " + warnList);
            // 进一步处理 warnList，比如存数据库或记录日志
            warnService.processWarnings(warnList);
        } catch (Exception e) {
            log.error("消息处理失败: ", e);
        }
    }
}

