package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.entity.AlarmRuleSegment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//报警规则mapper
@Mapper
public interface AlarmRuleSegmentMapper {
    AlarmRuleSegment findMatchedRule(
            @Param("batteryType") String batteryType,
            @Param("signalType") String signalType,
            @Param("warnId") Integer warnId,
            @Param("value") Double value
    );
    List<AlarmRuleSegment> findAll(); // 查询所有规则，不加过滤

    List<AlarmRuleSegment> findByTypeAndSignal(@Param("batteryType") String batteryType,
                                               @Param("signalType") String signalType);
}
