package com.hhz.bms.bms.controller;

import com.hhz.bms.bms.dto.WarnReportDTO;
import com.hhz.bms.bms.entity.WarnResultVO;
import com.hhz.bms.bms.response.JsonResult;
import com.hhz.bms.bms.service.WarnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WarnController {

    @Autowired
    private WarnService warnService;

    @PostMapping("/warn")
    public JsonResult warn(@RequestBody List<WarnReportDTO> warnList) {
        List<WarnResultVO> results = warnService.processWarnings1(warnList);
        return JsonResult.ok(results);
    }

    @GetMapping("/warn/{carId}")
    public JsonResult getWarnByCarId(@PathVariable Integer carId) {
        List<WarnResultVO> warnList = warnService.getWarnResultsByCarId(carId);
        return JsonResult.ok(warnList);
    }
}
