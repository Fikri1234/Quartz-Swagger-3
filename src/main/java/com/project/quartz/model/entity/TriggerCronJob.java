package com.project.quartz.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by user on 15:02 20/04/2025, 2025
 */

@Entity
@Table(name = "tbl_trigger_cron_job")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TriggerCronJob implements Serializable {
    @Serial
    private static final long serialVersionUID = -7289095110159822859L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String serviceJobClass;
    String expression;
    String params;
    boolean status;
    String description;
}
