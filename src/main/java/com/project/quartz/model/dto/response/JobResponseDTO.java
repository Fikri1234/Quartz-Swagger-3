package com.project.quartz.model.dto.response;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

/**
 * Created by user on 6:20 22/04/2025, 2025
 */

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobResponseDTO {
    String jobName;
    String jobGroup;
    JobDataMap jobDataMap;

    public JobResponseDTO(JobDetail jobDetail) {
        this.jobName = jobDetail.getKey().getName();
        this.jobGroup = jobDetail.getKey().getGroup();
        this.jobDataMap = jobDetail.getJobDataMap();
    }
}
