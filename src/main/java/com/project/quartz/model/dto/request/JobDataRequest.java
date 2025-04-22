package com.project.quartz.model.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Map;

/**
 * Created by user on 5:18 21/04/2025, 2025
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobDataRequest {

    String jobName;
    String jobGroup;
    String triggerName;
    String triggerGroup;
    Map<String, Object> jobDataMap;
    Long startAt;

    String template;
    String remoteIpAddr;
}
