package com.project.quartz.service;

import com.project.quartz.constant.JobKeyMapConstant;
import com.project.quartz.model.dto.request.JobDataRequest;
import com.project.quartz.util.JobDataMapUtil;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by user on 16:19 20/04/2025, 2025
 */

@Slf4j
@Component
public class JobLoaderService {

    private Scheduler scheduler;

    @Autowired
    public JobLoaderService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public JobKey runAJob(Class<? extends Job> jobClass, Date startAt, JobDataMap jobDataMap) {
        try {

            log.info("Start runAJob");

            String date = "_" + new SimpleDateFormat("yyyyMMddHHmmss.SSS").format(new Date());


            String remoteIpAddr = JobDataMapUtil.getStringJobData(jobDataMap, JobKeyMapConstant.REMOTE_IP_ADDR, null);
            String ipAddress;
            if (StringUtils.isNotBlank(remoteIpAddr)) {
                ipAddress = remoteIpAddr;
            } else {
                ipAddress = "0.0.0.0";
            }

            String identityName = jobClass.getSimpleName() + date;
            if (jobDataMap == null) {
                jobDataMap = new JobDataMap();
            } else {
                // Notification template
                String template = jobDataMap.getString(JobKeyMapConstant.TEMPLATE);
                if (StringUtils.isNotBlank(template)) {
                    identityName = template + date;
                }
            }
            jobDataMap.put(JobKeyMapConstant.REMOTE_IP_ADDR, ipAddress);

            // define the job and tie it to the Job class
            JobDetail job = jobDetail(jobClass, identityName, jobDataMap);

            // create the trigger and define its schedule
            Trigger trigger = trigger(jobClass, startAt, identityName);

            // add the job details to the scheduler and associate it with the trigger
            scheduler.scheduleJob(job, trigger);

            // start the scheduler
            scheduler.start();

            // Return the JobKey
            return job.getKey();
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private JobDetail jobDetail(Class<? extends Job> jobClass, String identityName, JobDataMap jobDataMap) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(identityName, jobClass.getSimpleName())
                .withDescription("Create job detail startAt: " + jobDataMap.getLong("startAt"))
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }

    private Trigger trigger(Class<? extends Job> jobClass, Date startAt, String identityName) {
        return TriggerBuilder.newTrigger()
                .withIdentity(identityName, jobClass.getSimpleName())
                .withDescription("Create Trigger " + jobClass.getSimpleName())
                .startAt(startAt)
                .build();
    }
    public JobDetail getJobDetail(JobKey jobKey) throws SchedulerException {
        return scheduler.getJobDetail(jobKey);
    }

    public List<JobKey> getAllJobKeys(String prefix) throws SchedulerException {
        List<JobKey> jobKeys = new ArrayList<>(scheduler.getJobKeys(org.quartz.impl.matchers.GroupMatcher.anyGroup()));
        return jobKeys.parallelStream()
                .filter(jobKey -> !org.springframework.util.StringUtils.hasText(prefix) || jobKey.getName().startsWith(prefix))
                .toList();
    }

    public void updateJob(JobDataRequest jobDataRequest) throws SchedulerException {
        JobKey jobKey = new JobKey(jobDataRequest.getJobName(), jobDataRequest.getJobGroup());
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);

        if (jobDetail != null) {
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);

            if (triggers.isEmpty()) {
                throw new SchedulerException("No triggers found for job: " + jobDataRequest.getJobName() + ", " + jobDataRequest.getJobGroup());
            }

            boolean isChangedStartAt = jobDataRequest.getStartAt() != null;

            // ------------------------------------------------------------
            // Update the job detail with the new values from the request
            JobBuilder jobBuilder = jobDetail.getJobBuilder();

            // Update Description
            if (isChangedStartAt) {
                jobBuilder.withDescription("Create job detail startAt: " + jobDataRequest.getStartAt());
            }

            // Update the job data map (if you have job data map)
            if (jobDataRequest.getJobDataMap() != null) {
                jobBuilder.usingJobData(new JobDataMap(jobDataRequest.getJobDataMap()));
            }

            JobDetail updatedJobDetail = jobBuilder.storeDurably().build();

            scheduler.addJob(updatedJobDetail, true); // Replace the existing job
            // ------------------------------------------------------------

            if (isChangedStartAt) {
                Trigger oldTrigger = triggers.get(0);
                TriggerKey triggerKey = oldTrigger.getKey();

                Date startAt = new Date(jobDataRequest.getStartAt());

                Trigger newTrigger = TriggerBuilder.newTrigger().withIdentity(triggerKey).withSchedule(oldTrigger.getScheduleBuilder()) // Copy the schedule
                        .startAt(startAt).forJob(jobKey).build();

                // Reschedule the job with the new trigger
                scheduler.rescheduleJob(triggerKey, newTrigger);
            }
        } else {
            throw new SchedulerException("Job not found: " + jobDataRequest.getJobName() + ", " + jobDataRequest.getJobGroup());
        }
    }

    public void deleteJob(JobKey jobKey) throws SchedulerException {
        scheduler.deleteJob(jobKey);
    }


}
