package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.entity.AlarmRuleSegment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AlarmRuleSegmentMapper {
    AlarmRuleSegment findMatchedRule(
            @Param("batteryType") String batteryType,
            @Param("signalType") String signalType,
            @Param("warnId") Integer warnId,
            @Param("value") Double value
    );
}
