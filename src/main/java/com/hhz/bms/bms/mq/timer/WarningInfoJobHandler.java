package com.hhz.bms.bms.mq.timer;


import com.hhz.bms.bms.mq.WarnMessageProducer;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WarningInfoJobHandler {

    @Autowired
    private WarnMessageProducer warnMessageProducer;
    @XxlJob("warningInfo")
    public void sendWarningInfo() {
        log.info("预警定时任务开始执行");

        String topic = "warn-topic";
        String message = "这是一条测试的预警消息";

        warnMessageProducer.sendWarningMessage(topic, message);
    }
}
