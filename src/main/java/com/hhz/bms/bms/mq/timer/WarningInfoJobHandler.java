package com.hhz.bms.bms.mq.timer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhz.bms.bms.dto.WarnReportDTO;
import com.hhz.bms.bms.entity.SignalReportProducer;
import com.hhz.bms.bms.mapper.SignalReportProducerMapper;
import com.hhz.bms.bms.mq.WarnMessageProducer;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // 按 carId 分组
        Map<Integer, List<SignalReportProducer>> groupedByCarId = new HashMap<>();
        for (SignalReportProducer report : recentReports) {
            groupedByCarId
                    .computeIfAbsent(report.getCarId(), k -> new ArrayList<>())
                    .add(report);
        }

        List<WarnReportDTO> warnReports = new ArrayList<>();
        /*合并信号*/
       /* for (Map.Entry<Integer, List<SignalReportProducer>> entry : groupedByCarId.entrySet()) {
            Integer carId = entry.getKey();
            List<SignalReportProducer> carReports = entry.getValue();
            Map<String, Double> signalMap = new HashMap<>();
            Integer warnId = null;

            // 合并同一车的信号数据
            for (SignalReportProducer report : carReports) {
                if (report.getMx() != null) {
                    signalMap.put("Mx", report.getMx());
                }
                if (report.getMi() != null) {
                    signalMap.put("Mi", report.getMi());
                }
                if (report.getIx() != null) {
                    signalMap.put("Ix", report.getIx());
                }
                if (report.getIi() != null) {
                    signalMap.put("Ii", report.getIi());
                }
            }

            // 根据信号数据来确定 warnId
            if (signalMap.containsKey("Mx") && signalMap.containsKey("Mi")) {
                warnId = 1; // 包含 Mx 和 Mi 设置 warnId 为 1
            } else if (signalMap.containsKey("Ix") && signalMap.containsKey("Ii")) {
                warnId = 2; // 包含 Ix 和 Ii 设置 warnId 为 2
            }

            // 将信号数据转换为 JSON 字符串
            try {
                ObjectMapper mapper = new ObjectMapper();
                String signalJson = mapper.writeValueAsString(signalMap);

                // 创建 WarnReportDTO
                WarnReportDTO warnReport = new WarnReportDTO();
                warnReport.setCarId(carId);

                // 如果 warnId 不为 null，设置 warnId
                if (warnId != null) {
                    warnReport.setWarnId(warnId);
                }

                warnReport.setSignal(signalJson);
                warnReports.add(warnReport);
            } catch (Exception e) {
                log.error("信号数据序列化失败", e);
            }
        }*/

        /*不合并信号*/
        for (SignalReportProducer report : recentReports) {
            Map<String, Double> signalMap = new HashMap<>();
            Integer warnId = null;

            // 直接使用单个信号数据
            if (report.getMx() != null) {
                signalMap.put("Mx", report.getMx());
            }
            if (report.getMi() != null) {
                signalMap.put("Mi", report.getMi());
            }
            if (report.getIx() != null) {
                signalMap.put("Ix", report.getIx());
            }
            if (report.getIi() != null) {
                signalMap.put("Ii", report.getIi());
            }

            // 仅当信号完全符合指定的字段时，才设置 warnId
            if (signalMap.size() == 2) {
                if (signalMap.containsKey("Mx") && signalMap.containsKey("Mi")) {
                    warnId = 1; // 仅包含 Mx 和 Mi 设置 warnId 为 1
                } else if (signalMap.containsKey("Ix") && signalMap.containsKey("Ii")) {
                    warnId = 2; // 仅包含 Ix 和 Ii 设置 warnId 为 2
                }
            }

            // 将信号数据转换为 JSON 字符串
            try {
                ObjectMapper mapper = new ObjectMapper();
                String signalJson = mapper.writeValueAsString(signalMap);

                // 创建 WarnReportDTO
                WarnReportDTO warnReport = new WarnReportDTO();
                warnReport.setCarId(report.getCarId());

                // 如果 warnId 不为 null，设置 warnId
                if (warnId != null) {
                    warnReport.setWarnId(warnId);
                }

                warnReport.setSignal(signalJson);
                warnReports.add(warnReport);
            } catch (Exception e) {
                log.error("信号数据序列化失败", e);
            }
        }
        // 发送预警消息
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(warnReports);
            warnMessageProducer.sendWarningMessage("warn-topic", json);
        } catch (Exception e) {
            log.error("预警消息发送失败", e);
        }
    }
}
