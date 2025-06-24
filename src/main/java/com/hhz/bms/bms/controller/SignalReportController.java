package com.hhz.bms.bms.controller;

import com.hhz.bms.bms.dto.SignalReportDTO;
import com.hhz.bms.bms.entity.VehicleSignalReport;
import com.hhz.bms.bms.service.VehicleSignalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signal")
public class SignalReportController {

    @Autowired
    private VehicleSignalReportService service;

    @PostMapping("/report")
    public String report(@RequestBody SignalReportDTO dto) {
        service.reportSignal(dto);
        return "Signal reported successfully.";
    }

    @GetMapping("/car/{carId}")
    public List<VehicleSignalReport> getSignals(@PathVariable Integer carId) {
        return service.getSignalsByCarIdWithCache(carId);
    }

    @PutMapping("/update")
    public String update(@RequestBody SignalReportDTO dto) {
        service.updateSignal(dto);
        return "Signal updated and cache refreshed.";
    }

    @DeleteMapping("/delete")
    public String delete(@RequestBody SignalReportDTO dto) {
        service.deleteSignal(dto);
        return "Signal deleted and cache cleared.";
    }

}
