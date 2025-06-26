package com.hhz.bms.bms.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhz.bms.bms.entity.AlarmRuleSegment;
import com.hhz.bms.bms.mapper.AlarmRuleSegmentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class AlarmRuleCacheaRedis {

    private final Map<String, Map<String, List<AlarmRuleSegment>>> ruleMap = new HashMap<>();

    @Autowired
    private AlarmRuleSegmentMapper alarmRuleSegmentMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String REDIS_KEY_PREFIX = "alarm_rules";

    @PostConstruct
    public void init() {
        log.info("正在本地初始化预警规则缓存...");
        List<AlarmRuleSegment> allRules = alarmRuleSegmentMapper.findAll();

        for (AlarmRuleSegment rule : allRules) {
            ruleMap
                    .computeIfAbsent(rule.getBatteryType(), k -> new HashMap<>())
                    .computeIfAbsent(rule.getSignalType(), k -> new ArrayList<>())
                    .add(rule);
        }

        for (Map<String, List<AlarmRuleSegment>> signalTypeMap : ruleMap.values()) {
            for (List<AlarmRuleSegment> ruleList : signalTypeMap.values()) {
                ruleList.sort(Comparator.comparingDouble(AlarmRuleSegment::getRangeMin));
            }
        }

        log.info("预警规则本地缓存完成，共加载 {} 条规则", allRules.size());
        // 可选：预热写入 Redis
        preloadRedisCache();
    }

    private void preloadRedisCache() {
        log.info("正在预热 Redis 缓存...");
        for (String batteryType : ruleMap.keySet()) {
            Map<String, List<AlarmRuleSegment>> signalTypeMap = ruleMap.get(batteryType);
            for (String signalType : signalTypeMap.keySet()) {
                String redisKey = getRedisKey(batteryType, signalType);
                try {
                    String json = objectMapper.writeValueAsString(signalTypeMap.get(signalType));
                    redisTemplate.opsForValue().set(redisKey, json, 1, TimeUnit.HOURS);
                    log.info("已写入 Redis 缓存：[{}]", redisKey);
                } catch (Exception e) {
                    log.warn("写入 Redis 缓存失败：[{}]", redisKey, e);
                }
            }
        }
    }

    public AlarmRuleSegment findMatchedRule(String batteryType, String signalType, double diff) {
        // 1. 先查本地内存
        List<AlarmRuleSegment> rules = getFromLocalCache(batteryType, signalType);
        if (rules != null && !rules.isEmpty()) {
            log.info("已从本地缓存中匹配到规则：[{}]", batteryType + ":" + signalType);
            return matchRule(rules, diff);
        }

        // 2. 查 Redis 并更新本地缓存
        List<AlarmRuleSegment> redisRules = getFromRedis(batteryType, signalType);
        if (redisRules != null && !redisRules.isEmpty()) {
            log.info("已从 Redis 中匹配到规则：[{}]", batteryType + ":" + signalType);
            putToLocalCache(batteryType, signalType, redisRules);
            return matchRule(redisRules, diff);
        }

        // 3. 最后查数据库
        List<AlarmRuleSegment> dbRules = alarmRuleSegmentMapper.findByTypeAndSignal(batteryType, signalType);
        if (dbRules != null && !dbRules.isEmpty()) {
            log.info("已从数据库中匹配到规则：[{}]", batteryType + ":" + signalType);
            putToLocalCache(batteryType, signalType, dbRules);
            try {
                String json = objectMapper.writeValueAsString(dbRules);
                redisTemplate.opsForValue().set(getRedisKey(batteryType, signalType), json, 1, TimeUnit.HOURS);
                log.info("已写入 Redis 缓存：[{}]", getRedisKey(batteryType, signalType));
            } catch (Exception e) {
                log.warn("写入 Redis 缓存失败：[{}]", getRedisKey(batteryType, signalType), e);
            }
            return matchRule(dbRules, diff);
        }

        return null;
    }

    private List<AlarmRuleSegment> getFromLocalCache(String batteryType, String signalType) {
        return ruleMap
                .getOrDefault(batteryType, Collections.emptyMap())
                .getOrDefault(signalType, Collections.emptyList());
    }

    private List<AlarmRuleSegment> getFromRedis(String batteryType, String signalType) {
        String key = getRedisKey(batteryType, signalType);
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json != null) {
                return objectMapper.readValue(json, new TypeReference<List<AlarmRuleSegment>>() {});
            }
        } catch (Exception e) {
            log.warn("读取 Redis 缓存失败：[{}]", key, e);
        }
        return null;
    }

    private void putToLocalCache(String batteryType, String signalType, List<AlarmRuleSegment> rules) {
        ruleMap
                .computeIfAbsent(batteryType, k -> new HashMap<>())
                .put(signalType, rules);
        rules.sort(Comparator.comparingDouble(AlarmRuleSegment::getRangeMin));
        log.info("已更新本地缓存：[{}]", batteryType + ":" + signalType);
    }

    private AlarmRuleSegment matchRule(List<AlarmRuleSegment> rules, double diff) {
        for (AlarmRuleSegment rule : rules) {
            if (diff >= rule.getRangeMin() && (rule.getRangeMax()==null||diff < rule.getRangeMax())) {
                return rule;
            }
        }
        return null;
    }

    private String getRedisKey(String batteryType, String signalType) {
        return REDIS_KEY_PREFIX + ":" + batteryType + ":" + signalType;
    }
}

