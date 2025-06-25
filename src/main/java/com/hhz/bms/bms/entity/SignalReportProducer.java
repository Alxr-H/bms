package com.hhz.bms.bms.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SignalReportProducer {
    private Integer carId;
    @JsonProperty("Mx")
    private Double Mx;

    @JsonProperty("Mi")
    private Double Mi;

    @JsonProperty("Ix")
    private Double Ix;

    @JsonProperty("Ii")
    private Double Ii;
}
