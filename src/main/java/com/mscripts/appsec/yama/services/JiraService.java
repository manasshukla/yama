package com.mscripts.appsec.yama.services;

import com.mscripts.appsec.yama.clients.jira.JiraClient;
import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service @Slf4j
public class JiraService {

    private final JiraClient jiraClient;

    public JiraService(JiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    public List<AccessRequest> getAccessRequest(YSystem system, String state){
        log.info("Getting the access tickets for {} with status {}", system, state);
        return jiraClient.getAccessRequests(system, state);
    }


    public void transitionIssue(String issueKey, int transitionId){
        jiraClient.transitionIssue(issueKey, transitionId);
    }

}
