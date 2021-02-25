package com.mscripts.appsec.yama;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Date;


@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class YamaApplication {

    public static void main(String[] args) {
        SpringApplication.run(YamaApplication.class, args);
    }

    @Autowired
    JobLauncher launcher;

    @Autowired
    Job createAWSProvisioningJob;

    @Autowired
    Job createAWSDeprovisioningJob;

    @Autowired
    Job createGithubProvisioningJob;

    @Autowired
    Job createGithubDeprovisioningJob;

    @Autowired
    Job createGraylogProvisioningJob;

    @Autowired
    Job createGraylogDeprovisioningJob;


    @Scheduled(cron = "${job.provision.cron}")
    public void scheduleAWSProvision() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        launcher.run(createAWSProvisioningJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        launcher.run(createGithubProvisioningJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        launcher.run(createGraylogProvisioningJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }

    @Scheduled(cron = "${job.deprovision.cron}")
    public void scheduleAWSDeprovision() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        launcher.run(createAWSDeprovisioningJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        launcher.run(createGithubDeprovisioningJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
        launcher.run(createGraylogDeprovisioningJob, new JobParametersBuilder().addDate("date", new Date()).toJobParameters());
    }
}

