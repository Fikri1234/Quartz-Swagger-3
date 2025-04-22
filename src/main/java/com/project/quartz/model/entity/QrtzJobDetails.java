package com.project.quartz.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Created by user on 16:16 20/04/2025, 2025
 */

@Entity
@Table(name = "qrtz_job_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class QrtzJobDetails {

    @EmbeddedId
    QrtzJobDetailsId id;

    @Column(name = "description", length = 400)
    String description;

    @Column(name = "job_class_name", length = 250, nullable = false)
    String jobClassName;

    @Column(name = "is_durable", length = 1, nullable = false)
    String isDurable;

    @Column(name = "is_nonconcurrent", length = 1, nullable = false)
    String isNonconcurrent;

    @Column(name = "is_update_data", length = 1, nullable = false)
    String isUpdateData;

    @Column(name = "requests_recovery", length = 1, nullable = false)
    String requestsRecovery;

    @Lob
    @Column(name = "job_data", columnDefinition = "BLOB")
    byte[] jobData;
}
