package com.hhz.bms.bms.controller;

import com.hhz.bms.bms.dto.WarnReportDTO;
import com.hhz.bms.bms.entity.WarnResultVO;
import com.hhz.bms.bms.response.JsonResult;
import com.hhz.bms.bms.service.WarnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Slf4j
public class WarnController {

    @Autowired
    private WarnService warnService;

    @PostMapping("/warn")
    public JsonResult warn(@RequestBody List<WarnReportDTO> warnList) {
        long startTime = System.nanoTime();
        List<WarnResultVO> results = warnService.processWarnings1(warnList);
        long endTime = System.nanoTime();
        long duration = endTime - startTime; // 响应时间（纳秒）
        double durationInMs = duration / 1_000_000.0;
        // 记录响应时间日志
        log.info("Warn request processed in {} ms", durationInMs);
        return JsonResult.ok(results);
    }

    @GetMapping("/warn/{carId}")
    public JsonResult getWarnByCarId(@PathVariable Integer carId) {
        List<WarnResultVO> warnList = warnService.getWarnResultsByCarId(carId);
        return JsonResult.ok(warnList);
    }
}
