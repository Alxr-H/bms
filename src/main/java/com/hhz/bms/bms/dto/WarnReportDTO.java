package com.hhz.bms.bms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL) // 仅当字段不为 null 时才会序列化
public class WarnReportDTO {
    private Integer carId;
    private Integer warnId; // 可选
    private String signal;  // JSON 格式字符串
}
