package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.entity.VehicleInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

//汽车基础信息
@Mapper
public interface VehicleInfoMapper {
    void insert(VehicleInfo vehicle);
    void update(VehicleInfo vehicle);
    void deleteById(Integer carId);
    // 新增的方法：根据 carId 查电池信息
    VehicleInfo findByCarId(Integer carId);
    List<VehicleInfo> findAll();
    List<VehicleInfo> findByCarIds(@Param("carIds") Set<Integer> carIds);
}
