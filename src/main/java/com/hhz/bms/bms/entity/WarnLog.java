package com.hhz.bms.bms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarnLog {
    private Long id;
    private Integer carId;
    private String batteryType;
    private String warnName;
    private Integer warnLevel;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

