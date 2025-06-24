package com.hhz.bms.bms.dto;

import lombok.Data;

@Data
public class WarnReportDTO {
    private Integer carId;
    private Integer warnId; // 可选
    private String signal;  // JSON 格式字符串
}
