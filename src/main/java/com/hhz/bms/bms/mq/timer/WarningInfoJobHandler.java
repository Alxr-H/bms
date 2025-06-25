package com.hhz.bms.bms.mq.timer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhz.bms.bms.entity.SignalReportProducer;
import com.hhz.bms.bms.mapper.SignalReportProducerMapper;
import com.hhz.bms.bms.mq.WarnMessageProducer;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class WarningInfoJobHandler {
    @Autowired
    private SignalReportProducerMapper signalReportProducerMapper;

    @Autowired
    private WarnMessageProducer warnMessageProducer;
    @XxlJob("warningInfo")
    public void sendWarningInfo() {
        log.info("预警定时任务开始执行");
        // 获取最近 1 天（24 小时）内上报的信号数据
        LocalDateTime fromTime = LocalDateTime.now().minusDays(1);
        List<SignalReportProducer> recentReports = signalReportProducerMapper.selectRecentReports(fromTime);
        if (recentReports.isEmpty()) {
            log.info("最近一天没有新的车辆上报信号");
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(recentReports);
            warnMessageProducer.sendWarningMessage("warn-topic", json);
        } catch (Exception e) {
            log.error("信号数据序列化失败", e);
        }
    }
}
