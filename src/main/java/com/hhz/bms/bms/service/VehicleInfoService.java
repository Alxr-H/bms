package com.hhz.bms.bms.service;

import com.hhz.bms.bms.entity.VehicleInfo;
import com.hhz.bms.bms.mapper.VehicleInfoMapper;
import com.hhz.bms.bms.util.VidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleInfoService {

    @Autowired
    private VehicleInfoMapper mapper;

    public void addVehicle(VehicleInfo vehicle) {
        vehicle.setVid(VidGenerator.generateVid()); // 自动生成16位车辆识别码
        if (vehicle.getCarId() == null) {
            throw new IllegalArgumentException("carId 不能为空");
        }
        if (vehicle.getBatteryType() == null) {
            throw new IllegalArgumentException("batteryType 不能为空");
        }
        if (vehicle.getTotalMileageKm() == null) {
            throw new IllegalArgumentException("totalMileageKm 不能为空");
        }
        if (vehicle.getBatteryHealthPercent() == null) {
            throw new IllegalArgumentException("batteryHealthPercent 不能为空");
        }
        mapper.insert(vehicle);
    }

    public void updateVehicle(VehicleInfo vehicle) {
        mapper.update(vehicle);
    }

    public void deleteVehicle(Integer carId) {
        mapper.deleteById(carId);
    }

    public VehicleInfo getVehicle(Integer carId) {
        return mapper.findByCarId(carId);
    }

    public List<VehicleInfo> getAllVehicles() {
        return mapper.findAll();
    }
}
