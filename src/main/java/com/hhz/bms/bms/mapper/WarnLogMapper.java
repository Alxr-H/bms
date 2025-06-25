package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.entity.WarnLog;
import org.apache.ibatis.annotations.Mapper;

//用于警告数据表的mapper
@Mapper
public interface WarnLogMapper {
    void insertWarnLog(WarnLog warnLog);
}

