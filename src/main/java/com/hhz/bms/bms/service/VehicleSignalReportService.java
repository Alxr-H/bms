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
        log.info("Getting signals for carId: {}", carId);
        String key = getRedisKey(carId);
        List<VehicleSignalReport> reports = (List<VehicleSignalReport>) redisTemplate.opsForValue().get(key);
        if (reports == null) {
            log.info("Cache miss. Querying database...");
            reports = mapper.selectByCarId(carId);
            if (reports != null && !reports.isEmpty()) {
                redisTemplate.opsForValue().set(key, reports, Duration.ofMinutes(10));
                log.info("Database result cached for carId: {}", carId);
            }
        } else {
            log.info("Cache hit for carId: {}", carId);
        }
        return reports;
    }

    public void reportSignal(SignalReportDTO dto) {
        log.info("Reporting signal for carId: {}", dto.getCarId());
        VehicleSignalReport report = new VehicleSignalReport();
        report.setCarId(dto.getCarId());
        report.setSignalMx(dto.getSignalMx());
        report.setSignalMi(dto.getSignalMi());
        report.setSignalIx(dto.getSignalIx());
        report.setSignalIi(dto.getSignalIi());
        report.setReportTime(LocalDateTime.now());

        // 1st delete before DB write
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

        // 2nd delete after DB write
        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("Deleted cache after insert for carId: {}", dto.getCarId());
    }

    public void updateSignal(SignalReportDTO dto) {
        log.info("Updating signal for carId: {}", dto.getCarId());

        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("Deleted cache before update");

        mapper.updateByCarId(dto);

        try {
            Thread.sleep(500); // 延迟双删
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted during delay after DB update", e);
        }

        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("Deleted cache after update");
    }

    public void deleteSignal(SignalReportDTO dto) {
        log.info("Deleting signal for carId: {}", dto.getCarId());

        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("Deleted cache before delete");

        mapper.deleteByCarId(dto.getCarId());

        try {
            Thread.sleep(500); // 延迟双删
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Interrupted during delay after DB delete", e);
        }

        redisTemplate.delete(getRedisKey(dto.getCarId()));
        log.info("Deleted cache after delete");
    }
}
