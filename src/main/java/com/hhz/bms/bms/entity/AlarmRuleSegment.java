package com.hhz.bms.bms.entity;

import lombok.Data;

@Data
public class AlarmRuleSegment {
    private Integer id;
    private Integer warnId;
    private String warnName;
    private String batteryType;
    private String signalType;
    private Double rangeMin;
    private Double rangeMax;
    private Integer warnLevel;
    private Integer ruleOrder;
}
