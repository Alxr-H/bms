package com.hhz.bms.bms.timer;


import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TimerDeprecatedAttach {

    @XxlJob("warningInfo")
    public void deleteAttachDeprecated() {
        log.info("定时任务开始执行");


    }
}
