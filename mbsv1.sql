create database bms;
use bms;
CREATE TABLE vehicle_info (
                              id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',

                              vid CHAR(16) NOT NULL UNIQUE COMMENT '车辆识别码（16位）',
                              vin VARCHAR(64) NOT NULL COMMENT '车架编号',

                              battery_type ENUM('三元电池', '铁锂电池') NOT NULL COMMENT '电池类型',
                              total_mileage_km INT UNSIGNED NOT NULL COMMENT '总里程（单位：公里）',
                              battery_health_percent TINYINT UNSIGNED NOT NULL COMMENT '电池健康状态（0~100）',

                              create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                              INDEX idx_vin (vin)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆信息表';
CREATE TABLE vehicle_signal_report (
                                       id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',

                                       vid CHAR(16) NOT NULL COMMENT '车辆识别码',
                                       signal_type ENUM('Mx', 'Mi', 'Ix', 'Ii') NOT NULL COMMENT '信号类型',
                                       signal_value DECIMAL(6,3) NOT NULL COMMENT '信号值',
                                       report_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上报时间',

                                       INDEX idx_vid_time (vid, report_time),
                                       INDEX idx_signal_type (signal_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='车辆上报信号表';

CREATE TABLE alarm_rule_segment (
                                    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '规则段主键',

                                    rule_group_code VARCHAR(32) NOT NULL COMMENT '规则编号（如 RUL-001）',
                                    rule_name VARCHAR(64) NOT NULL COMMENT '规则名称（如 电压差报警）',

                                    battery_type ENUM('三元电池', '铁锂电池') NOT NULL COMMENT '适用电池类型',
                                    signal_type VARCHAR(32) NOT NULL COMMENT '信号类型（如 Mx-Mi, Ix-Ii）',

                                    range_min DECIMAL(6,3) NOT NULL COMMENT '区间下限（闭区间）',
                                    range_max DECIMAL(6,3) DEFAULT NULL COMMENT '区间上限（开区间，NULL 表示无上限）',

                                    alarm_level TINYINT UNSIGNED NOT NULL COMMENT '报警等级（0 为最高响应）',
                                    rule_order TINYINT UNSIGNED NOT NULL COMMENT '匹配优先级（值越小优先级越高）',

                                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                                    INDEX idx_rule_lookup (rule_group_code, battery_type),
                                    INDEX idx_range (range_min, range_max)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='结构化预警规则段表';

-- 电压差报警：三元电池（RUL-001）
INSERT INTO alarm_rule_segment
(warnId, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
VALUES
    ('RUL-001', '电压差报警', '三元电池', 'Mx-Mi', 5.000, NULL, 0, 1),
    ('RUL-001', '电压差报警', '三元电池', 'Mx-Mi', 3.000, 5.000, 1, 2),
    ('RUL-001', '电压差报警', '三元电池', 'Mx-Mi', 1.000, 3.000, 2, 3),
    ('RUL-001', '电压差报警', '三元电池', 'Mx-Mi', 0.600, 1.000, 3, 4),
    ('RUL-001', '电压差报警', '三元电池', 'Mx-Mi', 0.200, 0.600, 4, 5);
-- 不报警 (<0.2)，可不插入或设 alarm_level = 5
-- 电压差报警：铁锂电池（RUL-001）
INSERT INTO alarm_rule_segment
(warnId, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
VALUES
    ('RUL-001', '电压差报警', '铁锂电池', 'Mx-Mi', 2.000, NULL, 0, 1),
    ('RUL-001', '电压差报警', '铁锂电池', 'Mx-Mi', 1.000, 2.000, 1, 2),
    ('RUL-001', '电压差报警', '铁锂电池', 'Mx-Mi', 0.700, 1.000, 2, 3),
    ('RUL-001', '电压差报警', '铁锂电池', 'Mx-Mi', 0.400, 0.700, 3, 4),
    ('RUL-001', '电压差报警', '铁锂电池', 'Mx-Mi', 0.200, 0.400, 4, 5);

-- 电流差报警：三元电池（RUL-002）
INSERT INTO alarm_rule_segment
(warnId, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
VALUES
    ('RUL-002', '电流差报警', '三元电池', 'Ix-Ii', 3.000, NULL, 0, 1),
    ('RUL-002', '电流差报警', '三元电池', 'Ix-Ii', 1.000, 3.000, 1, 2),
    ('RUL-002', '电流差报警', '三元电池', 'Ix-Ii', 0.200, 1.000, 2, 3);
-- 不报警 (<0.2) 可不插

-- 电流差报警：铁锂电池（RUL-002）
INSERT INTO alarm_rule_segment
(warnId, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
VALUES
    ('RUL-002', '电流差报警', '铁锂电池', 'Ix-Ii', 1.000, NULL, 0, 1),
    ('RUL-002', '电流差报警', '铁锂电池', 'Ix-Ii', 0.500, 1.000, 1, 2),
    ('RUL-002', '电流差报警', '铁锂电池', 'Ix-Ii', 0.200, 0.500, 2, 3);


-- -- 电压差报警 - 三元电池：Mx - Mi < 0.2
-- INSERT INTO alarm_rule_segment
-- (rule_group_code, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
-- VALUES
--     ('RUL-001', '电压差报警', '三元电池', 'Mx-Mi', 0.000, 0.200, -1, 6);
--
-- -- 电压差报警 - 铁锂电池：Mx - Mi < 0.2
-- INSERT INTO alarm_rule_segment
-- (rule_group_code, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
-- VALUES
--     ('RUL-001', '电压差报警', '铁锂电池', 'Mx-Mi', 0.000, 0.200, -1, 6);
--
-- -- 电流差报警 - 三元电池：Ix - Ii < 0.2
-- INSERT INTO alarm_rule_segment
-- (rule_group_code, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
-- VALUES
--     ('RUL-002', '电流差报警', '三元电池', 'Ix-Ii', 0.000, 0.200, null, 4);
--
-- -- 电流差报警 - 铁锂电池：Ix - Ii < 0.2
-- INSERT INTO alarm_rule_segment
-- (rule_group_code, rule_name, battery_type, signal_type, range_min, range_max, alarm_level, rule_order)
-- VALUES
--     ('RUL-002', '电流差报警', '铁锂电池', 'Ix-Ii', 0.000, 0.200, -1, 4);

ALTER TABLE vehicle_signal_report
    ADD CONSTRAINT fk_vehicle_signal_vid
        FOREIGN KEY (vid)
            REFERENCES vehicle_info(vid);



