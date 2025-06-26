package com.hhz.bms.bms.controller;

import com.hhz.bms.bms.dto.SignalReportDTO;
import com.hhz.bms.bms.entity.VehicleSignalReport;
import com.hhz.bms.bms.response.JsonResult;
import com.hhz.bms.bms.response.StatusCode;
import com.hhz.bms.bms.service.VehicleSignalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signal")
public class SignalReportController {

    @Autowired
    private VehicleSignalReportService service;

    // 信号上报
    @PostMapping("/report")
    public JsonResult report(@RequestBody List<SignalReportDTO> dtoList) {
        try {
            // 遍历列表中的每个 SignalReportDTO，逐个调用 reportSignal 方法
            for (SignalReportDTO dto : dtoList) {
                service.reportSignal(dto);
            }

            // 返回成功信息
            return JsonResult.ok("车辆信号上报成功");
        } catch (Exception e) {
            // 发生异常时返回错误信息
            return new JsonResult(StatusCode.ERROR, "不能上报车辆信号: " + e.getMessage());
        }
    }

    // 获取指定车辆的信号
    @GetMapping("/car/{carId}")
    public JsonResult getSignals(@PathVariable Integer carId) {
        try {
            List<VehicleSignalReport> signals = service.getSignalsByCarIdWithCache(carId);
            if (signals.isEmpty()) {
                return new JsonResult(StatusCode.NOT_FOUND, "没有找到对应车辆的信号。");
            }
            return JsonResult.ok(signals);
        } catch (Exception e) {
            return new JsonResult(StatusCode.ERROR, "不能获取车辆信号" + e.getMessage());
        }
    }

    // 更新信号--根据车架id匹配最近一条的数据进行更新
    @PutMapping("/update")
    public JsonResult update(@RequestBody SignalReportDTO dto) {
        try {
            service.updateSignal(dto);
            return JsonResult.ok("车辆信号更新成功");
        } catch (Exception e) {
            return new JsonResult(StatusCode.ERROR, "不能更新车辆信号" + e.getMessage());
        }
    }

    // 删除信号报告
    @DeleteMapping("/delete")
    public JsonResult delete(@RequestBody SignalReportDTO dto) {
        try {
            service.deleteSignal(dto);
            return JsonResult.ok("车辆信号删除成功");
        } catch (Exception e) {
            return new JsonResult(StatusCode.ERROR, "不能删除车辆信号" + e.getMessage());
        }
    }
}
