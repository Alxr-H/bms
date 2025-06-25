create
    database bms;
use
    bms;

CREATE TABLE vehicle_info
(
    id                     INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',

    vid                    CHAR(16)                      NOT NULL UNIQUE COMMENT '车辆识别码（16位）',
    car_id                 INT UNSIGNED                  NOT NULL UNIQUE COMMENT '车辆编号（前端 carId，业务主键）',

    battery_type           ENUM ('三元电池', '铁锂电池') NOT NULL COMMENT '电池类型',
    total_mileage_km       INT UNSIGNED                  NOT NULL COMMENT '总里程（单位：公里）',
    battery_health_percent TINYINT UNSIGNED              NOT NULL COMMENT '电池健康状态（0~100）',

    create_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time            TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_carId (car_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='车辆信息表';

CREATE TABLE vehicle_signal_report
(
    id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',

    car_id      INT UNSIGNED NOT NULL COMMENT '车辆编号（对应 vehicle_info.car_id）',

    signal_mx   DECIMAL(6, 3)         DEFAULT NULL COMMENT '最高电压 Mx',
    signal_mi   DECIMAL(6, 3)         DEFAULT NULL COMMENT '最低电压 Mi',
    signal_ix   DECIMAL(7, 3)         DEFAULT NULL COMMENT '最大电流 Ix',
    signal_ii   DECIMAL(7, 3)         DEFAULT NULL COMMENT '最小电流 Ii',

    report_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上报时间',

    create_time TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_car_time (car_id, report_time),

    CONSTRAINT fk_signal_car_id FOREIGN KEY (car_id)
        REFERENCES vehicle_info (car_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='车辆上报信号表';



CREATE TABLE alarm_rule_segment
(
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '规则段主键',

    warnId INT UNSIGNED                  NOT NULL COMMENT '规则编号（如 1）',
    warn_name       VARCHAR(64)                   NOT NULL COMMENT '规则名称（如 电压差报警）',

    battery_type    ENUM ('三元电池', '铁锂电池') NOT NULL COMMENT '适用电池类型',
    signal_type     VARCHAR(32)                   NOT NULL COMMENT '信号类型（如 Mx-Mi, Ix-Ii）',

    range_min       DECIMAL(7, 3)                 NOT NULL COMMENT '区间下限（闭区间）',
    range_max       DECIMAL(7, 3) DEFAULT NULL COMMENT '区间上限（开区间，NULL 表示无上限）',

    warn_level      TINYINT UNSIGNED              NOT NULL COMMENT '报警等级（0 为最高响应）',
    rule_order      TINYINT UNSIGNED              NOT NULL COMMENT '匹配优先级（值越小优先级越高）',

    create_time     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    INDEX idx_rule_lookup (warnId, battery_type),
    INDEX idx_range (range_min, range_max),
    INDEX idx_rule_full (warnId, battery_type, signal_type, range_min, range_max)

) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT = '结构化预警规则段表';


-- 电压差报警：三元电池（RUL-001）
INSERT INTO alarm_rule_segment
(warnId, warn_name, battery_type, signal_type, range_min, range_max, warn_level, rule_order)
VALUES ('1', '电压差报警', '三元电池', 'Mx-Mi', 5.000, NULL, 0, 1),
       ('1', '电压差报警', '三元电池', 'Mx-Mi', 3.000, 5.000, 1, 2),
       ('1', '电压差报警', '三元电池', 'Mx-Mi', 1.000, 3.000, 2, 3),
       ('1', '电压差报警', '三元电池', 'Mx-Mi', 0.600, 1.000, 3, 4),
       ('1', '电压差报警', '三元电池', 'Mx-Mi', 0.200, 0.600, 4, 5);
-- 不报警 (<0.2)，可不插入或设 warn_level = 5
-- 电压差报警：铁锂电池（1）
INSERT INTO alarm_rule_segment
(warnId, warn_name, battery_type, signal_type, range_min, range_max, warn_level, rule_order)
VALUES ('1', '电压差报警', '铁锂电池', 'Mx-Mi', 2.000, NULL, 0, 1),
       ('1', '电压差报警', '铁锂电池', 'Mx-Mi', 1.000, 2.000, 1, 2),
       ('1', '电压差报警', '铁锂电池', 'Mx-Mi', 0.700, 1.000, 2, 3),
       ('1', '电压差报警', '铁锂电池', 'Mx-Mi', 0.400, 0.700, 3, 4),
       ('1', '电压差报警', '铁锂电池', 'Mx-Mi', 0.200, 0.400, 4, 5);

-- 电流差报警：三元电池（2）
INSERT INTO alarm_rule_segment
(warnId, warn_name, battery_type, signal_type, range_min, range_max, warn_level, rule_order)
VALUES ('2', '电流差报警', '三元电池', 'Ix-Ii', 3.000, NULL, 0, 1),
       ('2', '电流差报警', '三元电池', 'Ix-Ii', 1.000, 3.000, 1, 2),
       ('2', '电流差报警', '三元电池', 'Ix-Ii', 0.200, 1.000, 2, 3);
-- 不报警 (<0.2) 可不插

-- 电流差报警：铁锂电池（2）
INSERT INTO alarm_rule_segment
(warnId, warn_name, battery_type, signal_type, range_min, range_max, warn_level, rule_order)
VALUES ('2', '电流差报警', '铁锂电池', 'Ix-Ii', 1.000, NULL, 0, 1),
       ('2', '电流差报警', '铁锂电池', 'Ix-Ii', 0.500, 1.000, 1, 2),
       ('2', '电流差报警', '铁锂电池', 'Ix-Ii', 0.200, 0.500, 2, 3);


CREATE TABLE warn_log (
                          id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
                          car_id INT UNSIGNED NOT NULL COMMENT '车辆编号',
                          battery_type VARCHAR(32) NOT NULL COMMENT '电池类型',
                          warn_name VARCHAR(64) NOT NULL COMMENT '报警名称',
                          warn_level TINYINT UNSIGNED NOT NULL COMMENT '报警等级',
                          create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

                          INDEX idx_car_id (car_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '预警记录表';
