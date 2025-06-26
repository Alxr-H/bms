package com.hhz.bms.bms.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarnResultVO {
    private Integer carId;
    private String batteryType;
//    private String signalType;  // ← 加上信号类型，如 Mx、Ix、Mx-Mi 等
//    private Double value;       // ← 实际预警的值（或差值，如 Mx-Mi）
    private String warnName;
    private Integer warnLevel;
}
