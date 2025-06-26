package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.entity.WarnLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//用于警告数据表的mapper
@Mapper
public interface WarnLogMapper {
    void insertWarnLog(WarnLog warnLog);
    void batchInsert(@Param("logs") List<WarnLog> logs);
    List<WarnLog> selectByCarId(Integer carId);
}

