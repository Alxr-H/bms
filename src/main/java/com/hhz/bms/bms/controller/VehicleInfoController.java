package com.hhz.bms.bms.controller;

import com.hhz.bms.bms.entity.VehicleInfo;
import com.hhz.bms.bms.service.VehicleInfoService;
import com.hhz.bms.bms.response.JsonResult;
import com.hhz.bms.bms.response.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleInfoController {

    @Autowired
    private VehicleInfoService service;

    // 添加车辆
    @PostMapping("/insert")
    public JsonResult add(@RequestBody VehicleInfo vehicle) {
        service.addVehicle(vehicle);
        return JsonResult.ok("车辆新增成功.");
    }

    // 更新车辆
    @PutMapping("/update")
    public JsonResult update(@RequestBody VehicleInfo vehicle) {
        service.updateVehicle(vehicle);
        return JsonResult.ok("车辆更新成功");
    }

    // 删除车辆
    @DeleteMapping("/delete/{id}")
    public JsonResult delete(@PathVariable Integer id) {
        service.deleteVehicle(id);
        return JsonResult.ok("车辆删除成功");
    }

    // 查找单个车辆
    @GetMapping("/find/{id}")
    public JsonResult findById(@PathVariable Integer id) {
        VehicleInfo vehicle = service.getVehicle(id);
        if (vehicle != null) {
            return JsonResult.ok(vehicle);
        } else {
            return new JsonResult(StatusCode.NOT_FOUND, "当前车辆没有到");
        }
    }

    // 查找所有车辆
    @GetMapping("/findAll")
    public JsonResult findAll() {
        List<VehicleInfo> vehicles = service.getAllVehicles();
        return JsonResult.ok(vehicles);
    }
}
