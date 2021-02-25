package com.mscripts.appsec.yama.config;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.TeamService;
import org.eclipse.egit.github.core.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mscripts.appsec.yama.constants.YamaConstants.YAMA_APP;

@Configuration
public class YamaConfiguration {


    @Bean
    public AmazonIdentityManagement getIAMClient() {
        return AmazonIdentityManagementClientBuilder.standard().build();
    }

    @Bean
    public Github getGithub(GithubConfig githubConfig){
        return new RtGithub(githubConfig.getUsername(), githubConfig.getPassword());
    }

    @Bean
    public GitHubClient getGithubClient(GithubConfig githubConfig){
        return new GitHubClient().setUserAgent(YAMA_APP)
                .setCredentials(githubConfig.getUsername(), githubConfig.getPassword());
    }

    @Bean
    public OrganizationService getOrganisationService(GitHubClient client){
        return new OrganizationService(client);
    }

    @Bean
    public UserService getUserService(GitHubClient client){
        return new UserService(client);
    }

    @Bean
    public TeamService getTeamService(GitHubClient client){
        return new TeamService(client);
    }

    @Bean
    public JiraRestClient getJiraClient(JiraConfig jiraConfig){
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(URI.create(jiraConfig.getUrl()),
                        jiraConfig.getUsername(), jiraConfig.getPassword());
    }

    @Bean
    public ExecutorService getExecutorService(){
        return Executors.newFixedThreadPool(10);
    }
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }


}
