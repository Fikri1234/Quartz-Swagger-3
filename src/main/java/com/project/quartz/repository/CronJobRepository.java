package com.project.quartz.repository;

import com.project.quartz.model.entity.CronJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public interface CronJobRepository extends JpaRepository<CronJob, Integer> {
  
  List<CronJob> findByStatus(boolean status);
}
