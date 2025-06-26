package com.hhz.bms.bms.service;

import com.hhz.bms.bms.dto.SignalReportDTO;
import com.hhz.bms.bms.entity.VehicleSignalReport;
import com.hhz.bms.bms.mapper.VehicleSignalReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VehicleSignalReportService {

    @Autowired
    private VehicleSignalReportMapper mapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String getRedisKey(Integer carId) {
        return "signal:car:" + carId;
    }

    public List<VehicleSignalReport> getSignalsByCarIdWithCache(Integer carId) {
        log.info("得到信号的车架编号: {}", carId);
        String key = getRedisKey(carId);
        List<VehicleSignalReport> reports = (List<VehicleSignalReport>) redisTemplate.opsForValue().get(key);
        if (reports == null) {
            log.info("缓存没有命中，访问数据库");
            reports = mapper.selectByCarId(carId);
            if (reports != null && !reports.isEmpty()) {
                redisTemplate.opsForValue().set(key, reports, Duration.ofMinutes(10));
                log.info("将数据库的结果放入redis缓存中: {}", carId);
            }
        } else {
            log.info("redis缓存中有车架ID: {}", carId);
        }
        return reports;
    }

    public void reportSignal(SignalReportDTO dto) {
        log.info("得到信号的车架编号: {}", dto.getCarId());
        VehicleSignalReport report = new VehicleSignalReport();
        report.setCarId(dto.getCarId());
        report.setSignalMx(dto.getSignalMx());
        report.setSignalMi(dto.getSignalMi());
        report.setSignalIx(dto.getSignalIx());
        report.setSignalIi(dto.getSignalIi());
        report.setReportTime(LocalDateTime.now());

        // 1、写入数据库前删除redis
        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("Deleted cache before insert for carId: {}", dto.getCarId());

        mapper.insert(report);
        log.info("Inserted signal into DB");

        try {
            Thread.sleep(500); // 防止并发读导致缓存回源为旧值
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted during delay after DB insert", e);
        }

        // 2、写入数据库后延迟删除redis
        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("Deleted cache after insert for carId: {}", dto.getCarId());
    }

    public void updateSignal(SignalReportDTO dto) {
        log.info("要更新的车架编号: {}", dto.getCarId());

        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("删除redis中的车架编号");

        mapper.updateByCarId(dto);

        try {
            Thread.sleep(500); // 延迟双删
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("异常：", e);
        }

        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("删除redis中的缓存");
    }

    public void deleteSignal(SignalReportDTO dto) {
        log.info("要删除的车架编号信号: {}", dto.getCarId());

        redisTemplate.delete(getRedisKey(dto.getCarId()));

        mapper.deleteByCarId(dto.getCarId());

        try {
            Thread.sleep(500); // 延迟双删
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("异常：", e);
        }

        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("再次删除缓存");
    }
}
