package com.project.quartz.controller;

import com.project.quartz.constant.JobKeyMapConstant;
import com.project.quartz.constant.ServicePath;
import com.project.quartz.model.dto.request.JobDataRequest;
import com.project.quartz.model.dto.response.JobResponseDTO;
import com.project.quartz.service.JobLoaderService;
import com.project.quartz.service.job.SimpleJob;
import com.project.quartz.util.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by user on 3:25 21/04/2025, 2025
 */

@RestController
@Slf4j
@Tag(name = "Job", description = "Job API")
@RequestMapping(ServicePath.JOB)
public class JobController extends BaseController {

    private final JobLoaderService jobLoaderService;

    @Autowired
    public JobController(JobLoaderService jobLoaderService) {
        this.jobLoaderService = jobLoaderService;
    }

    @Tag(name = "get", description = "Get job detail")
    @Operation(summary = "Get job detail", description = "Get job detail and their data from data source")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved JobDetail",
                    content = @Content(schema = @Schema(implementation = JobDetail.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "JobKey not found with the provided ID",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(schema = @Schema())})})
    @GetMapping
    public ResponseEntity<Object> getJobDetail(String jobName, String jobGroup) throws CustomException {

        try {
            JobKey jobKey = new JobKey(jobName, jobGroup);
            JobDetail jobDetail = jobLoaderService.getJobDetail(jobKey);
            if (jobDetail == null) {
                return new ResponseEntity<>(responseApi(false,"Job not found for key: " + jobName, null), HttpStatus.BAD_REQUEST);
            }
            JobResponseDTO responseDTO = new JobResponseDTO(jobDetail);
            return new ResponseEntity<>(responseApi(true, null, responseDTO), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException("Unable to find id " + jobName, HttpStatus.NOT_FOUND);
        }
    }

    @Tag(name = "get", description = "Get all job detail")
    @Operation(summary = "Get all job detail", description = "Get all job detail and their data from data source")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved All JobKey",
                    content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = JobKey.class)))}),
            @ApiResponse(responseCode = "404", description = "JobKey is not found",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(schema = @Schema())})})
    @GetMapping(value = ServicePath.ALL)
    public ResponseEntity<Object> getJobDetails(@RequestParam(value = "prefix", required = false) String prefix) throws CustomException {

        try {
            return new ResponseEntity<>(responseApi(true, null, jobLoaderService.getAllJobKeys(prefix)), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException("Unable to find all the data ", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<Object> createJobDetails(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Update job", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobDataRequest.class),
                            examples = @ExampleObject(value = "{ \"template\": \"DUMMY_TEMPLATE\", \"remoteIpAddr\": \"127.9.0.0\", \"startAt\": 1745214296000 }")))
            @RequestBody JobDataRequest request) throws CustomException {

        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put(JobKeyMapConstant.REMOTE_IP_ADDR, request.getRemoteIpAddr());
            jobDataMap.put(JobKeyMapConstant.TEMPLATE, request.getTemplate());
            jobDataMap.put(JobKeyMapConstant.START_AT, request.getStartAt());

            Date startAt = new Date(request.getStartAt());
            return new ResponseEntity<>(responseApi(true, null, jobLoaderService.runAJob(SimpleJob.class, startAt, jobDataMap)), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException("Unable to insert the data ", HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Update job", description = "Update an existing Job. The response is boolean")
    @PutMapping
    public ResponseEntity<Object> updateJobDetails(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Update job", required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JobDataRequest.class),
                            examples = @ExampleObject(value = "{ \"jobName\": \"SIMPLE_JOB_20250214121543\", \"jobGroup\": \"SimpleJob\", \"startAt\": 1745214296000 }")))
            @RequestBody JobDataRequest jobDataRequest) {

        try {
            jobLoaderService.updateJob(jobDataRequest);
            return new ResponseEntity<>(responseApi(true, null, true), HttpStatus.OK);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return new ResponseEntity<>(responseApi(true, null, false), HttpStatus.OK);
        }
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Delete Job by ID",
                    content = @Content(schema = @Schema(implementation = JobDetail.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Job detail not found",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = {@Content(schema = @Schema())})})
    @DeleteMapping
    public ResponseEntity<Object> deleteJobDetails(String jobName, String jobGroup) throws CustomException {

        try {
            JobKey jobKey = new JobKey(jobName, jobGroup);
            jobLoaderService.deleteJob(jobKey);
            return new ResponseEntity<>(responseApi(true, null, true), HttpStatus.OK);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return new ResponseEntity<>(responseApi(true, null, false), HttpStatus.OK);
        }
    }
}
