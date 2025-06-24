package com.hhz.bms.bms.dto;

import lombok.Data;

@Data
public class SignalReportDTO {
    private Integer carId;
    private Double signalMx;
    private Double signalMi;
    private Double signalIx;
    private Double signalIi;
}
