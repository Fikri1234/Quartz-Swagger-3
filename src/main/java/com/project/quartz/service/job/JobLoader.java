package com.project.quartz.service.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.quartz.model.entity.CronJob;
import com.project.quartz.repository.CronJobRepository;
import com.project.quartz.repository.QrtzJobDetailsRepository;
import com.project.quartz.util.ObjectMapperUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 7:27 21/04/2025, 2025
 */

@Slf4j
@Component
public class JobLoader {

    private final SchedulerFactoryBean scheduler;
    private final CronJobRepository cronJobRepository;
    private final QrtzJobDetailsRepository qrtzJobDetailsRepository;

    private static final String CANT_LOAD_JOB_MSG = "Can not load job {}";
    private static final String CANT_SCHEDULE_JOB_MSG = "Can not schedule job {}";
    private static final String GROUP_STRING = "group_%s";

    @Autowired
    public JobLoader(SchedulerFactoryBean scheduler,
                     CronJobRepository cronJobRepository,
                     QrtzJobDetailsRepository qrtzJobDetailsRepository) {
        this.scheduler = scheduler;
        this.cronJobRepository = cronJobRepository;
        this.qrtzJobDetailsRepository = qrtzJobDetailsRepository;
    }

    @PostConstruct
    public void loadAllJob() {
        log.info("Start load all jobs");
        List<CronJob> cronJobList = fetchCronJobs();

        if (!CollectionUtils.isEmpty(cronJobList)) {
            cronJobList.forEach(this::scheduleJobIfNotExists);
            scheduler.start();
        }
    }

    private List<CronJob> fetchCronJobs() {
        return cronJobRepository.findByStatus(true);
    }

    private void scheduleJobIfNotExists(CronJob cronJob) {
        String jobKeyStr = jobKeyStr(cronJob.getServiceJobClass(), cronJob.getExpression(), cronJob.getParams());
        boolean jobExists = qrtzJobDetailsRepository.findById_JobNameIgnoreCaseAndId_JobGroupIgnoreCase(jobKeyStr,
                String.format(GROUP_STRING, cronJob.getServiceJobClass())).isEmpty();

        if (jobExists) {
            log.info("Adding job {}", cronJob.getServiceJobClass());
            addNewJob(cronJob.getServiceJobClass(), cronJob.getExpression(), cronJob.getParams());
        }
    }

    public void runAJob(String className, String paramsJson) {
        ObjectMapper mapper = ObjectMapperUtil.getInstance();
        try {
            Map<String, String> params = new HashMap<>();
            if (StringUtils.hasText(paramsJson)) {
                params = mapper.readValue(paramsJson, Map.class);
            }
            JobDataMap jobDataMap = new JobDataMap();
            for (Map.Entry<String,String> entry : params.entrySet()) {
                jobDataMap.put(entry.getKey(), params.get(entry.getKey()));
            }
            JobKey jobKey = JobKey.jobKey(String.format("test_%s", className), String.format("test_group_%s", className));
            JobDetail jobDetail = JobBuilder.newJob().ofType((Class<? extends Job>) Class.forName(className))
                    .storeDurably()
                    .setJobData(jobDataMap)
                    .withDescription("Invoke Sample Job service...")
                    .withIdentity(jobKey)
                    .build();
            scheduler.getScheduler().addJob(jobDetail, true);
            scheduler.getScheduler().triggerJob(jobKey);
            Thread.sleep(2000);
            scheduler.getScheduler().deleteJob(jobKey);
        } catch (SchedulerException e) {
            log.warn(CANT_SCHEDULE_JOB_MSG,className, e);
        } catch (ClassNotFoundException | ClassCastException | JsonProcessingException e) {
            log.warn(CANT_LOAD_JOB_MSG,className, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    //=== PRIVATE METHODS ===============
    private void addNewJob(String className, String quartzSchedule, String paramsJson) {

        ObjectMapper mapper = ObjectMapperUtil.getInstance();

        try {
            Map<String, String> params = new HashMap<>();
            if (StringUtils.hasText(paramsJson)) {
                params = mapper.readValue(paramsJson, Map.class);
            }
            JobDetail jobDetail = jobDetail((Class<? extends Job>) Class.forName(className), className,
                    quartzSchedule, paramsJson, params);
            Trigger trigger = trigger(jobDetail, className, quartzSchedule, paramsJson);
            if (scheduler.getScheduler().checkExists(jobDetail.getKey())) {
                log.info("job found with key: {}", jobDetail.getKey());
                scheduler.getScheduler().deleteJob(jobDetail.getKey());
            }
            scheduler.getScheduler().scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.warn(CANT_SCHEDULE_JOB_MSG,className, e);
        } catch (ClassNotFoundException | ClassCastException | JsonProcessingException e) {
            log.warn(CANT_LOAD_JOB_MSG,className, e);
        }
    }

    private JobDetail jobDetail(Class<? extends Job> jobClass, String className, String quartzSchedule,
                                String paramStr, Map<String, String> params) {
        JobDataMap jobDataMap = new JobDataMap();
        for (Map.Entry<String,String> entry : params.entrySet()) {
            jobDataMap.put(entry.getKey(), params.get(entry.getKey()));
        }
        JobKey jobKey = JobKey.jobKey(
                jobKeyStr(className, quartzSchedule, paramStr),
                String.format(GROUP_STRING, className));
        return JobBuilder.newJob().ofType(jobClass)
                .storeDurably()
                .setJobData(jobDataMap)
                .withIdentity(jobKey)
                .withDescription("Invoke Sample Job service...")
                .build();
    }


    private Trigger trigger(JobDetail job, String className, String expression, String paramStr) {
        String key = jobKeyStr(className, expression, paramStr);
        return TriggerBuilder.newTrigger().forJob(job)
                .withIdentity(key, String.format(GROUP_STRING, className))
                .withDescription("Trigger for " + key)
                .withSchedule(CronScheduleBuilder.cronSchedule(expression))
                .build();
    }

    private String jobKeyStr(String className, String quartzSchedule, String paramStr) {
        return String.format("key_%s", className.substring(className.lastIndexOf(".") + 1, className.length())
                + quartzSchedule.replaceAll("\\s", "")
                + paramStr.replaceAll("\\s", ""));
    }

}
