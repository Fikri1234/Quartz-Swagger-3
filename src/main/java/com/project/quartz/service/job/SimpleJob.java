package com.project.quartz.service.job;

import com.project.quartz.constant.JobKeyMapConstant;
import com.project.quartz.util.JobDataMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * Created by user on 3:09 21/04/2025, 2025
 */

@Slf4j
@Component
public class SimpleJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String remoteIpAddr = JobDataMapUtil.getStringJobData(jobDataMap, JobKeyMapConstant.REMOTE_IP_ADDR, null);
        String template = JobDataMapUtil.getStringJobData(jobDataMap, JobKeyMapConstant.TEMPLATE, null);
        log.info("Do Job: Test input remoteIpAddr: {} template: {}", remoteIpAddr, template);
    }
}
