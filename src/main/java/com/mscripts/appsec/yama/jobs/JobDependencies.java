package com.mscripts.appsec.yama.jobs;

import com.mscripts.appsec.yama.services.JiraService;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class JobDependencies {
    @Autowired
    JiraService jiraService;
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;

}
