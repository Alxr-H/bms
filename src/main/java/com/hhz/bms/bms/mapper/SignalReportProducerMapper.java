package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.entity.SignalReportProducer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

//用于发送定时任务的信号mapper
@Mapper
public interface SignalReportProducerMapper {
    List<SignalReportProducer> selectRecentReports(@Param("fromTime") LocalDateTime fromTime);
}
