package com.hhz.bms.bms.cache;

import com.hhz.bms.bms.entity.AlarmRuleSegment;
import com.hhz.bms.bms.mapper.AlarmRuleSegmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

//@Component
@Slf4j
public class AlarmRuleCache {

    private final Map<String, Map<String, List<AlarmRuleSegment>>> ruleMap = new HashMap<>();

//    @Autowired
    private AlarmRuleSegmentMapper alarmRuleSegmentMapper;

    @PostConstruct
    public void init() {
        List<AlarmRuleSegment> allRules = alarmRuleSegmentMapper.findAll();

        for (AlarmRuleSegment rule : allRules) {
            ruleMap
                    .computeIfAbsent(rule.getBatteryType(), k -> new HashMap<>())
                    .computeIfAbsent(rule.getSignalType(), k -> new ArrayList<>())
                    .add(rule);
        }

        // 排序：根据 rangeMin 升序，便于匹配优化
        for (Map<String, List<AlarmRuleSegment>> signalTypeMap : ruleMap.values()) {
            for (List<AlarmRuleSegment> ruleList : signalTypeMap.values()) {
                ruleList.sort(Comparator.comparingDouble(AlarmRuleSegment::getRangeMin));
            }
        }

        log.info("预警规则缓存完成，共加载 {} 条规则", allRules.size());
    }

    public AlarmRuleSegment findMatchedRule(String batteryType, String signalType, double diff) {
        List<AlarmRuleSegment> rules = ruleMap
                .getOrDefault(batteryType, Collections.emptyMap())
                .getOrDefault(signalType, Collections.emptyList());

        for (AlarmRuleSegment rule : rules) {
            if (diff >= rule.getRangeMin() && diff < rule.getRangeMax()) {
                return rule;
            }
        }
        return null;
    }
}
