package com.hhz.bms.bms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarningResult {
    private Integer carId;
    private String batteryType;
    private String warnName;
    private Integer warnLevel;
}
