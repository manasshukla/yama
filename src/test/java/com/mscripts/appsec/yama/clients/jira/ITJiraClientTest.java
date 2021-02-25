package com.mscripts.appsec.yama.clients.jira;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ITJiraClientTest {

    @Autowired
    JiraClient jiraClient;

    @Test
    void testGetUsers(){
//        jiraClient.getUsers();

    }

}