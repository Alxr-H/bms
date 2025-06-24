package com.hhz.bms.bms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class VehicleInfo {
    private Integer id;
    private String vid;
    private Integer carId;
    private String batteryType;
    private Integer totalMileageKm;
    private Integer batteryHealthPercent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
