package com.hhz.bms.bms.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhz.bms.bms.dto.WarnReportDTO;
import com.hhz.bms.bms.service.WarnService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.Executor;

@Component
@Slf4j
@RocketMQMessageListener(topic = "warn-topic", consumerGroup = "warn-group")
public class WarnMessageConsumer implements RocketMQListener<String> {

//    @Autowired
//    private ObjectMapper objectMapper; // Jackson ObjectMapper 用于转换 JSON
//
//    @Autowired
//    private WarnService warnService;

    @Autowired
    private RestTemplate restTemplate;  // 注入 RestTemplate

    @Autowired
    @Qualifier("warnExecutor")  // 使用你配置的线程池
    private Executor warnExecutor;

    private final String targetUrl = "http://localhost:9081/api/warn";

    @Override
    public void onMessage(String message) {
        warnExecutor.execute(() -> {
        try {
            // 打印接收到的原始消息
            log.info("收到预警消息: " + message);

//            // 将消息字符串转换为 List<WarnReportDTO> 类型
//            List<WarnReportDTO> warnList = objectMapper.readValue(message, objectMapper.getTypeFactory().constructCollectionType(List.class, WarnReportDTO.class));
//            log.info("转换后的数据: " + warnList);
//            // 进一步处理 warnList，比如存数据库或记录日志
//            warnService.processWarnings(warnList);
            // 不再反序列化，直接作为 JSON 字符串 POST 出去

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(targetUrl, requestEntity, String.class);

            log.info("HTTP响应状态码: {}", response.getStatusCode());
            log.info("HTTP响应内容: {}", response.getBody());
        } catch (Exception e) {
            log.error("消息处理失败: ", e);
        }
        });
    }
}

