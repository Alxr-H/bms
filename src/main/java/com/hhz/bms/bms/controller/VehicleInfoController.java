package com.hhz.bms.bms.controller;

import com.hhz.bms.bms.entity.VehicleInfo;
import com.hhz.bms.bms.service.VehicleInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicles")
public class VehicleInfoController {

    @Autowired
    private VehicleInfoService service;

    @PostMapping("/insert")
    public String add(@RequestBody VehicleInfo vehicle) {
        service.addVehicle(vehicle);
        return "Vehicle added with auto VID.";
    }

    @PutMapping("/update")
    public String update(@RequestBody VehicleInfo vehicle) {
        service.updateVehicle(vehicle);
        return "Vehicle updated.";
    }

    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteVehicle(id);
        return "Vehicle deleted.";
    }

    @GetMapping("/find/{id}")
    public VehicleInfo findById(@PathVariable Integer id) {
        return service.getVehicle(id);
    }

    @GetMapping("/findAll")
    public List<VehicleInfo> findAll() {
        return service.getAllVehicles();
    }
}
