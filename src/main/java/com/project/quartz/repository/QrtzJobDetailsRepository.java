package com.project.quartz.repository;

import com.project.quartz.model.entity.QrtzJobDetails;
import com.project.quartz.model.entity.QrtzJobDetailsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by user on 16:19 20/04/2025, 2025
 */

@Repository
public interface QrtzJobDetailsRepository extends JpaRepository<QrtzJobDetails, QrtzJobDetailsId> {
    List<QrtzJobDetails> findById_JobNameIgnoreCaseAndId_JobGroupIgnoreCase(String jobName, String jobGroup);
}
