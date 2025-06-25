package com.hhz.bms.bms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhz.bms.bms.dto.WarnReportDTO;
import com.hhz.bms.bms.entity.AlarmRuleSegment;
import com.hhz.bms.bms.entity.VehicleInfo;
import com.hhz.bms.bms.entity.WarnLog;
import com.hhz.bms.bms.entity.WarnResultVO;
import com.hhz.bms.bms.mapper.AlarmRuleSegmentMapper;
import com.hhz.bms.bms.mapper.VehicleInfoMapper;
import com.hhz.bms.bms.mapper.WarnLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class WarnService {

    @Autowired
    private VehicleInfoMapper vehicleInfoMapper;

    @Autowired
    private AlarmRuleSegmentMapper alarmRuleSegmentMapper;

    @Autowired
    private WarnLogMapper warnLogMapper;
    /**
     * 判断并生成预警数据列表
     */
    public List<WarnResultVO> processWarnings(List<WarnReportDTO> warnList) {
        List<WarnResultVO> result = new ArrayList<>();

        for (WarnReportDTO dto : warnList) {
            Integer carId = dto.getCarId();
            Integer warnId = dto.getWarnId();

            VehicleInfo vehicle = vehicleInfoMapper.findByCarId(carId);
            if (vehicle == null) {
                log.warn("车辆[{}] 未找到对应信息", carId);
                continue;
            }

            String batteryType = vehicle.getBatteryType();
            Map<String, Double> rawSignal;

            try {
                rawSignal = new ObjectMapper().readValue(dto.getSignal(), new TypeReference<Map<String, Double>>() {});
            } catch (Exception e) {
                log.error("车辆[{}] 信号解析失败：{}", carId, e.getMessage());
                continue;
            }

            boolean checkVoltage = (warnId == null || warnId == 1);
            boolean checkCurrent = (warnId == null || warnId == 2);

            // ========== 组合信号计算 + 判断 ==========
            // Mx-Mi
            if (checkVoltage) {
                double diff = rawSignal.get("Mx") - rawSignal.get("Mi");
                diff = Math.round(diff * 1000.0) / 1000.0; // 保留三位小数
                String signalType = "Mx-Mi";
                AlarmRuleSegment rule = alarmRuleSegmentMapper.findMatchedRule(batteryType, signalType, 1, diff);
                if (rule != null) {
                    result.add(new WarnResultVO(carId, batteryType, rule.getWarnName(), rule.getWarnLevel()));
                    // 保存预警记录
                    WarnLog log = new WarnLog(null, carId, batteryType, rule.getWarnName(), rule.getWarnLevel(), null, null);
                    warnLogMapper.insertWarnLog(log);
                }
            }

            // Ix-Ii
            if (checkCurrent) {
                double diff = rawSignal.get("Ix") - rawSignal.get("Ii");
                diff = Math.round(diff * 1000.0) / 1000.0;
                String signalType = "Ix-Ii";
                AlarmRuleSegment rule = alarmRuleSegmentMapper.findMatchedRule(batteryType, signalType, 2, diff);
                if (rule != null) {
                    result.add(new WarnResultVO(carId, batteryType, rule.getWarnName(), rule.getWarnLevel()));
                    // 保存预警记录
                    WarnLog log = new WarnLog(null, carId, batteryType, rule.getWarnName(), rule.getWarnLevel(), null, null);
                    warnLogMapper.insertWarnLog(log);
                }
            }
        }

        return result;
    }
}
