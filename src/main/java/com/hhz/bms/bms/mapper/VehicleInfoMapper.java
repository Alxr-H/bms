package com.hhz.bms.bms.mapper;

import com.hhz.bms.bms.entity.VehicleInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface VehicleInfoMapper {
    void insert(VehicleInfo vehicle);
    void update(VehicleInfo vehicle);
    void deleteById(Integer id);
    // 新增的方法：根据 carId 查电池信息
    VehicleInfo findByCarId(Integer carId);
    List<VehicleInfo> findAll();
}
