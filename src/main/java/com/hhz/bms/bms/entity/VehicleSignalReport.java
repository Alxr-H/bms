package com.hhz.bms.bms.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleSignalReport {
    private Long id;
    private Integer carId;
    private Double signalMx;
    private Double signalMi;
    private Double signalIx;
    private Double signalIi;
    private LocalDateTime reportTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
