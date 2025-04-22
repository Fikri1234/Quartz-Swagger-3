package com.project.quartz.util;

import org.quartz.JobDataMap;
import org.springframework.stereotype.Component;

/**
 * Created by user on 2:41 21/04/2025, 2025
 */

@Component
public class JobDataMapUtil {
    private static <T> T getJobData(JobDataMap jobDataMap, String key, T defaultValue, Class<T> type) {
        return jobDataMap.containsKey(key) ? type.cast(jobDataMap.get(key)) : defaultValue;
    }

    public static Integer getIntJobData(JobDataMap jobDataMap, String key, Integer defaultValue) {
        return getJobData(jobDataMap, key, defaultValue, Integer.class);
    }

    public static String getStringJobData(JobDataMap jobDataMap, String key, String defaultValue) {
        return getJobData(jobDataMap, key, defaultValue, String.class);
    }
}
