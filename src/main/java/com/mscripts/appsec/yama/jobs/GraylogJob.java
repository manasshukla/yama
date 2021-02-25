package com.mscripts.appsec.yama.jobs;

import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.services.impl.GraylogService;
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

import java.util.List;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Configuration
@Slf4j
@Import(CommonJobConfigs.class)
public class GraylogJob extends JobDependencies {

    private GraylogService graylogService;
    private ItemProcessor<AccessResponse, AccessResponse> emailProcessor;
    private ItemProcessor<AccessResponse, AccessResponse> jiraTransitionProcessor;
    private ItemWriter<AccessResponse> itemWriter;

    @Autowired
    public GraylogJob(GraylogService graylogService, ItemProcessor<AccessResponse, AccessResponse> emailProcessor,
                      ItemProcessor<AccessResponse, AccessResponse> jiraTransitionProcessor, ItemWriter<AccessResponse> itemWriter) {
        this.graylogService = graylogService;
        this.emailProcessor = emailProcessor;
        this.jiraTransitionProcessor = jiraTransitionProcessor;
        this.itemWriter = itemWriter;
    }


    @Bean
    public Job createGraylogProvisioningJob() {
        return this.jobBuilderFactory.get("GRAYLOG_PROVISION_JOB")
                .incrementer(new RunIdIncrementer())
                .start(graylogProvisionStep())
                .build();
    }


    @Bean
    public Job createGraylogDeprovisioningJob() {
        return this.jobBuilderFactory.get("GRAYLOG_DEPROVISION_JOB")
                .incrementer(new RunIdIncrementer())
                .start(graylogDeprovisionStep())
                .next(graylogTimeBoundDeprovisionStep())
                .build();
    }

    @Bean
    public Step graylogProvisionStep() {
        return this.stepBuilderFactory.get("GRAYLOG_PROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readGraylogProvisionRequest())
                .processor(compositeItemProcessorForProvisioning())
                .writer(itemWriter)
                .build();
    }


    @Bean
    public Step graylogDeprovisionStep() {
        return this.stepBuilderFactory.get("GRAYLOG_DEPROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readGraylogDeprovisionRequest())
                .processor(compositeItemProcessorForDeprovisioning())
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Step graylogTimeBoundDeprovisionStep() {
        return this.stepBuilderFactory.get("GRAYLOG_TIME_BOUND_DEPROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readGraylogTimeBoundDeprovisionRequest())
                .processor(compositeItemProcessorForDeprovisioning())
                .writer(itemWriter)
                .build();
    }


    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readGraylogProvisionRequest() {
        return getJiraTickets(YSystem.GRAYLOG, PROVISION);
    }

    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readGraylogDeprovisionRequest() {
        return getJiraTickets(YSystem.GRAYLOG, DEPROVISION);
    }

    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readGraylogTimeBoundDeprovisionRequest() {
        return getJiraTickets(YSystem.GRAYLOG, TIMEBOUND_DEPROVISION);
    }

    private ListItemReader<AccessRequest> getJiraTickets(YSystem system, String state) {
        List<AccessRequest> accessRequests = jiraService.getAccessRequest(system, state);
        log.info("Access request count read : " + accessRequests.size());
        return new ListItemReader<>(accessRequests);
    }

    public ItemProcessor<AccessRequest, AccessResponse> compositeItemProcessorForProvisioning() {
        return new CompositeItemProcessorBuilder<AccessRequest, AccessResponse>()
                .delegates(createGraylogUserProcessor(), emailProcessor, jiraTransitionProcessor)
                .build();
    }

    private ItemProcessor<AccessRequest, AccessResponse> createGraylogUserProcessor() {
        return graylogService::provisionUser;
    }

    public ItemProcessor<AccessRequest, AccessResponse> compositeItemProcessorForDeprovisioning() {
        return new CompositeItemProcessorBuilder<AccessRequest, AccessResponse>()
                .delegates(deleteGraylogUserProcessor(), emailProcessor, jiraTransitionProcessor)
                .build();
    }

    private ItemProcessor<AccessRequest, AccessResponse> deleteGraylogUserProcessor() {
        return graylogService::deprovisionUser;
    }
}
