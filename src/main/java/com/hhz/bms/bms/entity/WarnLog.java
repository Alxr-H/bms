package com.hhz.bms.bms.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarnLog {
    private Long id;
    private Integer carId;
    private String batteryType;
    private String warnName;
    private Integer warnLevel;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

