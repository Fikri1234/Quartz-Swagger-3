package com.project.quartz.service.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by user on 11:52 21/04/2025, 2025
 */

@Slf4j
@Component
public class SimpleTriggerJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap params = jobExecutionContext.getMergedJobDataMap();
        log.info("Start job SimpleTriggerJob at: {}", new Date());
        if (params != null && params.size() > 0) {
            for (String key : params.getKeys()) {
                log.info(String.format("%s : %s", key, params.get(key)));
            }
        }
    }

}
