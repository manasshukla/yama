package com.mscripts.appsec.yama.jobs;


import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.services.impl.IAMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.util.List;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Configuration
@Slf4j
@Import(CommonJobConfigs.class)
public class AWSJob extends JobDependencies {

    private IAMService iamService;

    private ItemProcessor<AccessResponse, AccessResponse> emailProcessor;

    private ItemProcessor<AccessResponse, AccessResponse> jiraTransitionProcessor;

    private ItemWriter<AccessResponse> itemWriter;

    @Autowired
    public AWSJob(IAMService iamService, ItemProcessor<AccessResponse, AccessResponse> emailProcessor,
                  ItemProcessor<AccessResponse, AccessResponse> jiraTransitionProcessor,
                  ItemWriter<AccessResponse> itemWriter) {
        this.iamService = iamService;
        this.emailProcessor = emailProcessor;
        this.jiraTransitionProcessor = jiraTransitionProcessor;
        this.itemWriter = itemWriter;
    }

    @Bean
    public Job createAWSProvisioningJob(@Autowired DataSource dataSource) {
        return this.jobBuilderFactory.get("AWS_PROVISION_JOB")
                .incrementer(new RunIdIncrementer())
                .start(provisionStep(dataSource))
                .build();
    }

    @Bean
    public Job createAWSDeprovisioningJob(@Autowired DataSource dataSource) {
        return this.jobBuilderFactory.get("AWS_DEPROVISION_JOB")
                .incrementer(new RunIdIncrementer())
                .start(deprovisionStep(dataSource))
                .next(timeBoundDeprovisionStep(dataSource))
                .build();
    }

    @Bean
    public Step provisionStep(DataSource dataSource) {
        return this.stepBuilderFactory.get("AWS_PROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readAWSProvisionRequest())
                .processor(compositeItemProcessorForProvisioning())
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Step deprovisionStep(DataSource dataSource) {
        return this.stepBuilderFactory.get("AWS_DEPROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readAWSDeprovisionRequest())
                .processor(compositeItemProcessorForDeprovisioning())
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Step timeBoundDeprovisionStep(DataSource dataSource) {
        return this.stepBuilderFactory.get("AWS_TIME_BOUND_DEPROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readAWSTimeBoundDeprovisionRequest())
                .processor(compositeItemProcessorForDeprovisioning())
                .writer(itemWriter)
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readAWSProvisionRequest() {
        return getJiraTickets(YSystem.AWS, PROVISION);
    }

    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readAWSDeprovisionRequest() {
        return getJiraTickets(YSystem.AWS, DEPROVISION);
    }

    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readAWSTimeBoundDeprovisionRequest() {
        return getJiraTickets(YSystem.AWS, TIMEBOUND_DEPROVISION);
    }

    private ListItemReader<AccessRequest> getJiraTickets(YSystem system, String state) {
        List<AccessRequest> accessRequests = jiraService.getAccessRequest(system, state);
        log.info("Access request count read : " + accessRequests.size());
        return new ListItemReader<>(accessRequests);
    }

    @Bean
    public ItemProcessor<AccessRequest, AccessResponse> compositeItemProcessorForProvisioning() {
        return new CompositeItemProcessorBuilder<AccessRequest, AccessResponse>()
                .delegates(createIAMUserProcessor(iamService), emailProcessor, jiraTransitionProcessor)
                .build();
    }

    @Bean
    public ItemProcessor<AccessRequest, AccessResponse> compositeItemProcessorForDeprovisioning() {
        return new CompositeItemProcessorBuilder<AccessRequest, AccessResponse>()
                .delegates(deleteIAMUserProcessor(iamService), emailProcessor, jiraTransitionProcessor)
                .build();
    }

    @Bean
    public ItemProcessor<AccessRequest, AccessResponse> createIAMUserProcessor(IAMService iamService) {
        return iamService::provisionUser;
    }

    @Bean
    public ItemProcessor<AccessRequest, AccessResponse> deleteIAMUserProcessor(IAMService iamService) {
        return iamService::deprovisionUser;
    }

}
