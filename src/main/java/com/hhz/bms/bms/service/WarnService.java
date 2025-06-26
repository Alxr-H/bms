package com.hhz.bms.bms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhz.bms.bms.cache.AlarmRuleCache;
import com.hhz.bms.bms.cache.AlarmRuleCacheaRedis;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WarnService {

    @Autowired
    private VehicleInfoMapper vehicleInfoMapper;

    @Autowired
    private AlarmRuleSegmentMapper alarmRuleSegmentMapper;

    @Autowired
    private AlarmRuleCacheaRedis alarmRuleCacheaRedis;

    @Autowired
    private WarnLogMapper warnLogMapper;

    @Autowired
    private ObjectMapper objectMapper;
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
                }else{
                    result.add(new WarnResultVO(carId, batteryType, "不报警", null));
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
                }else{
                    result.add(new WarnResultVO(carId, batteryType, "不报警", null));
                }
            }
        }

        return result;
    }

    public List<WarnResultVO> getWarnResultsByCarId(Integer carId) {
        List<WarnLog> logList = warnLogMapper.selectByCarId(carId);
        return logList.stream().map(log -> new WarnResultVO(
                log.getCarId(),
                log.getBatteryType(),
                log.getWarnName(),
                log.getWarnLevel()
        )).collect(Collectors.toList());
    }

    public List<WarnResultVO> processWarnings1(List<WarnReportDTO> warnList) {
        List<WarnResultVO> result = new ArrayList<>();
        List<WarnLog> warnLogs = new ArrayList<>();

        Set<Integer> carIds = warnList.stream()
                .map(WarnReportDTO::getCarId)
                .collect(Collectors.toSet());

        Map<Integer, VehicleInfo> vehicleMap = vehicleInfoMapper.findByCarIds(carIds)
                .stream().collect(Collectors.toMap(VehicleInfo::getCarId, v -> v));

        for (WarnReportDTO dto : warnList) {
            Integer carId = dto.getCarId();
            VehicleInfo vehicle = vehicleMap.get(carId);
            if (vehicle == null) {
                log.warn("车辆[{}] 未找到对应信息", carId);
                continue;
            }

            String batteryType = vehicle.getBatteryType();
            Map<String, Double> rawSignal;
            try {
                rawSignal = objectMapper.readValue(dto.getSignal(), new TypeReference<Map<String, Double>>() {});
            } catch (Exception e) {
                log.error("车辆[{}] 信号解析失败：{}", carId, e.getMessage());
                continue;
            }

            Integer warnId = dto.getWarnId();
            if (warnId == null || warnId == 1) {
                checkVoltage(carId, batteryType, rawSignal, result, warnLogs);
            }

            if (warnId == null || warnId == 2) {
                checkCurrent(carId, batteryType, rawSignal, result, warnLogs);
            }
        }

        if (!warnLogs.isEmpty()) {
            warnLogMapper.batchInsert(warnLogs);
        }

        return result;
    }

    private void checkVoltage(Integer carId, String batteryType, Map<String, Double> signal,
                              List<WarnResultVO> result, List<WarnLog> logs) {
        Double mx = signal.get("Mx");
        Double mi = signal.get("Mi");
        if (mx == null || mi == null) {
            log.warn("车辆[{}] 缺少电压数据", carId);
            return;
        }

        double diff = Math.round((mx - mi) * 1000.0) / 1000.0;
//        AlarmRuleSegment rule = alarmRuleSegmentMapper.findMatchedRule(batteryType, "Mx-Mi", 1, diff);
        AlarmRuleSegment rule = alarmRuleCacheaRedis.findMatchedRule(batteryType, "Mx-Mi", diff);
        if (rule != null) {
            result.add(new WarnResultVO(carId, batteryType, rule.getWarnName(), rule.getWarnLevel()));
            logs.add(new WarnLog(null, carId, batteryType, rule.getWarnName(), rule.getWarnLevel(), null, null));
        } else {
            result.add(new WarnResultVO(carId, batteryType, "不报警", null));
        }
    }

    private void checkCurrent(Integer carId, String batteryType, Map<String, Double> signal,
                              List<WarnResultVO> result, List<WarnLog> logs) {
        Double ix = signal.get("Ix");
        Double ii = signal.get("Ii");
        if (ix == null || ii == null) {
            log.warn("车辆[{}] 缺少电流数据", carId);
            return;
        }

        double diff = Math.round((ix - ii) * 1000.0) / 1000.0;
//        AlarmRuleSegment rule = alarmRuleSegmentMapper.findMatchedRule(batteryType, "Ix-Ii", 2, diff);
        AlarmRuleSegment rule = alarmRuleCacheaRedis.findMatchedRule(batteryType, "Ix-Ii", diff);
        if (rule != null) {
            result.add(new WarnResultVO(carId, batteryType, rule.getWarnName(), rule.getWarnLevel()));
            logs.add(new WarnLog(null, carId, batteryType, rule.getWarnName(), rule.getWarnLevel(), null, null));
        } else {
            result.add(new WarnResultVO(carId, batteryType, "不报警", null));
        }
    }

}
