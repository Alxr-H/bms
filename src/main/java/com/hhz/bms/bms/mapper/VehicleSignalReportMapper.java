package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.dto.SignalReportDTO;
import com.hhz.bms.bms.entity.VehicleSignalReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

//车辆信号上报mapper
@Mapper
public interface VehicleSignalReportMapper {
    void insert(VehicleSignalReport report);
    List<VehicleSignalReport> selectByCarId(Integer carId);
    void updateByCarId(SignalReportDTO dto);

    void deleteByCarId(@Param("carId") Integer carId);
}

