package com.mscripts.appsec.yama.jobs;

import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.services.impl.GithubService;
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
public class GithubJob extends JobDependencies {

    private GithubService githubService;

    private ItemProcessor<AccessResponse, AccessResponse> jiraTransitionProcessor;

    private ItemWriter<AccessResponse> itemWriter;

    @Autowired
    public GithubJob(GithubService githubService, ItemProcessor<AccessResponse, AccessResponse> jiraTransitionProcessor, ItemWriter<AccessResponse> itemWriter) {
        this.githubService = githubService;
        this.jiraTransitionProcessor = jiraTransitionProcessor;
        this.itemWriter = itemWriter;
    }

    @Bean
    public Job createGithubProvisioningJob() {
        return this.jobBuilderFactory.get("GITHUB_PROVISION_JOB")
                .incrementer(new RunIdIncrementer())
                .start(githubProvisionStep())
                .build();
    }


    @Bean
    public Job createGithubDeprovisioningJob() {
        return this.jobBuilderFactory.get("GITHUB_DEPROVISION_JOB")
                .incrementer(new RunIdIncrementer())
                .start(githubDeprovisionStep())
                .next(githubTimeBoundDeprovisionStep())
                .build();
    }

    @Bean
    public Step githubProvisionStep() {
        return this.stepBuilderFactory.get("GITHUB_PROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readGithubProvisionRequest())
                .processor(compositeItemProcessorForProvisioning())
                .writer(itemWriter)
                .build();
    }


    @Bean
    public Step githubDeprovisionStep() {
        return this.stepBuilderFactory.get("GITHUB_DEPROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readGithubDeprovisionRequest())
                .processor(compositeItemProcessorForDeprovisioning())
                .writer(itemWriter)
                .build();
    }

    @Bean
    public Step githubTimeBoundDeprovisionStep() {
        return this.stepBuilderFactory.get("GITHUB_TIME_BOUND_DEPROVISION_STEP")
                .<AccessRequest, AccessResponse>chunk(5)
                .reader(readGithubTimeBoundDeprovisionRequest())
                .processor(compositeItemProcessorForDeprovisioning())
                .writer(itemWriter)
                .build();
    }


    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readGithubProvisionRequest() {
        return getJiraTickets(YSystem.GITHUB, PROVISION);
    }

    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readGithubDeprovisionRequest() {
        return getJiraTickets(YSystem.GITHUB, DEPROVISION);
    }

    @Bean
    @StepScope
    public ListItemReader<AccessRequest> readGithubTimeBoundDeprovisionRequest() {
        return getJiraTickets(YSystem.GITHUB, TIMEBOUND_DEPROVISION);
    }

    private ListItemReader<AccessRequest> getJiraTickets(YSystem system, String state) {
        List<AccessRequest> accessRequests = jiraService.getAccessRequest(system, state);
        log.info("Access request count read : " + accessRequests.size());
        return new ListItemReader<>(accessRequests);
    }


    public ItemProcessor<AccessRequest, AccessResponse> compositeItemProcessorForProvisioning() {
        return new CompositeItemProcessorBuilder<AccessRequest, AccessResponse>()
                .delegates(createGithubUserProcessor(), jiraTransitionProcessor)
                .build();
    }

    private ItemProcessor<AccessRequest, AccessResponse> createGithubUserProcessor() {
        return githubService::provisionUser;
    }

    public ItemProcessor<AccessRequest, AccessResponse> compositeItemProcessorForDeprovisioning() {
        return new CompositeItemProcessorBuilder<AccessRequest, AccessResponse>()
                .delegates(deleteGithubUserProcessor(), jiraTransitionProcessor)
                .build();
    }

    private ItemProcessor<AccessRequest, AccessResponse> deleteGithubUserProcessor() {
        return githubService::deprovisionUser;
    }

}
