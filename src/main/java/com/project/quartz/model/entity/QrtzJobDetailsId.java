package com.project.quartz.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by user on 16:16 20/04/2025, 2025
 */

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Embeddable
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QrtzJobDetailsId implements Serializable {
    @Serial
    private static final long serialVersionUID = 6572767229498259486L;

    @Column(name = "SCHED_NAME")
    String schedName;
    @Column(name = "JOB_NAME")
    String jobName;
    @Column(name = "JOB_GROUP")
    String jobGroup;
}
