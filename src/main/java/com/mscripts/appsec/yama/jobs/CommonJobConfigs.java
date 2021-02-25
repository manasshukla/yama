package com.mscripts.appsec.yama.jobs;

import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.services.JiraService;
import com.mscripts.appsec.yama.utils.EmailUtil;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Configuration
public class CommonJobConfigs {

    private JiraService jiraService;
    private DataSource dataSource;


    private static final String INSERT_AUDIT_TRAIL = "insert into audit_trail (`key`, `system`, `operation`, `reporter`, `requestedFor`, `accessTimeLimit`, `accessStartDate`, `accessEndDate`, `processed`, `emailSent`, `errorReason`) "
            + "values (:accessRequest.key,:accessRequest.system,:operation,:accessRequest.reporter,:accessRequest.requestedFor,"
            + ":accessRequest.accessTimeLimit,:accessRequest.accessStartDate,:accessRequest.accessEndDate,:result,:emailSent,:errorReason)";


    @Autowired
    public CommonJobConfigs(JiraService jiraService, DataSource dataSource) {
        this.jiraService = jiraService;
        this.dataSource = dataSource;
    }

    @Bean
    public ItemProcessor<AccessResponse, AccessResponse> emailProcessor() {
        return item -> {
            if (item.getResult().equals("SUCCESS")) {
                EmailUtil.sendDummyEmail(item);
                item.setEmailSent(true);
            }
            return item;
        };
    }

    @Bean
    public ItemProcessor<AccessResponse, AccessResponse> jiraTransitionProcessor() {
        return item -> {
            if (PROVISION.equals(item.getOperation()) && SUCCESS.equals(item.getResult())) {
                jiraService.transitionIssue(item.getAccessRequest().getKey(), PROVISIONED_TRANSITION_ID);
            } else if (DEPROVISION.equals(item.getOperation()) && SUCCESS.equals(item.getResult())) {
                jiraService.transitionIssue(item.getAccessRequest().getKey(), DEPROVISIONED_TRANSITION_ID);
            }
            return item;
        };
    }


    @Bean
    public ItemWriter<AccessResponse> itemWriter() {
        return new JdbcBatchItemWriterBuilder<AccessResponse>()
                .dataSource(dataSource)
                .sql(INSERT_AUDIT_TRAIL)
                .beanMapped()
                .build();
    }

}
