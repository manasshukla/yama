package com.mscripts.appsec.yama.clients.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.model.jira.GithubAccessRequest;
import com.mscripts.appsec.yama.model.jira.GraylogAccessRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.mscripts.appsec.yama.JiraTestManager.*;
import static com.mscripts.appsec.yama.constants.YamaConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JiraClientTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    JiraRestClient restClient;

    @Mock(answer = RETURNS_DEEP_STUBS)
    Issue issue;

    @InjectMocks
    JiraClient jiraClient;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAccessRequestsGithubAccessRequests() {
        when(restClient.getSearchClient().searchJql(any(String.class)).claim().getIssues()).thenReturn(Collections.singleton(issue));
        mockIssue(issue);
        doReturn(requestedFor).when(issue).getFieldByName(REQUESTED_FOR_FIELD);
        doReturn(githubOrg).when(issue).getFieldByName(GITHUB_ORG_FIELD);
        doReturn(githubTeams).when(issue).getFieldByName(GITHUB_TEAMS_FIELD);
        doReturn(githubUser).when(issue).getFieldByName(GITHUB_USER_NAME_FIELD);

        List<AccessRequest> accessRequests = jiraClient.getAccessRequests(YSystem.GITHUB, PROVISION);

        //Assert
        assertions(accessRequests);
        assertEquals(GITHUB, accessRequests.get(0).getSystem());
        assertEquals(expectedGithubOrg, ((GithubAccessRequest) accessRequests.get(0)).getOrganisation());
        assertEquals(expectedGithubUserName, ((GithubAccessRequest) accessRequests.get(0)).getUsername());
        assertEquals(expectedGithubTeams, ((GithubAccessRequest) accessRequests.get(0)).getTeams());

        //verify
        verify(restClient, atLeastOnce()).getSearchClient();
        verify(issue, atLeastOnce()).getFieldByName(any(String.class));
    }

    @Test
    void getAccessRequestsAWSAccessRequests() {
        //Mock
        when(restClient.getSearchClient().searchJql(any(String.class)).claim().getIssues()).thenReturn(Collections.singleton(issue));
        mockIssue(issue);
        doReturn(requestedFor).when(issue).getFieldByName(REQUESTED_FOR_FIELD);


        //Execute

        List<AccessRequest> accessRequests = jiraClient.getAccessRequests(YSystem.AWS, PROVISION);

        //Assert
        assertions(accessRequests);
        assertEquals(AWS,  accessRequests.get(0).getSystem());

        //verify
        verify(restClient, atLeastOnce()).getSearchClient();
        verify(issue, atLeastOnce()).getFieldByName(any(String.class));
    }

    @Test
    void getAccessRequestsAWSAccessRequestsWithNullRequestedForField() {
        //Mock
        when(restClient.getSearchClient().searchJql(any(String.class)).claim().getIssues()).thenReturn(Collections.singleton(issue));
        mockIssue(issue);
        doReturn(nullRequestedFor).when(issue).getFieldByName(REQUESTED_FOR_FIELD);

        //Execute
        List<AccessRequest> accessRequests = jiraClient.getAccessRequests(YSystem.AWS, PROVISION);

        //Assert
        assertions(accessRequests);
        assertEquals(expectedReporter,  accessRequests.get(0).getRequestedFor());
        assertEquals(AWS,  accessRequests.get(0).getSystem());

        //verify
        verify(restClient, atLeastOnce()).getSearchClient();
        verify(issue, atLeastOnce()).getFieldByName(any(String.class));
    }

    @Test
    void getAccessRequestsGraylogAccessRequests() {
        //Mock
        when(restClient.getSearchClient().searchJql(any(String.class)).claim().getIssues()).thenReturn(Collections.singleton(issue));
        mockIssue(issue);
        doReturn(requestedFor).when(issue).getFieldByName(REQUESTED_FOR_FIELD);
        doReturn(graylogClients).when(issue).getFieldByName(GRAYLOG_CLIENTS_FIELD);
        doReturn(graylogRoles).when(issue).getFieldByName(GRAYLOG_ROLE_FIELD);

        //Execute
        List<AccessRequest> accessRequests = jiraClient.getAccessRequests(YSystem.GRAYLOG, PROVISION);

        //Assert
        assertions(accessRequests);
        assertEquals(GRAYLOG, accessRequests.get(0).getSystem());
        assertEquals(expectedGLClients, ((GraylogAccessRequest) accessRequests.get(0)).getGraylogClients());
        assertEquals(expectedGLRoles, ((GraylogAccessRequest) accessRequests.get(0)).getGraylogRoles());

        //verify
        verify(restClient, atLeastOnce()).getSearchClient();
        verify(issue, atLeastOnce()).getFieldByName(any(String.class));
    }

    private static void mockIssue(Issue issue) {
        when(issue.getKey()).thenReturn("TEST-KEY");
        when(issue.getStatus().getName()).thenReturn("TEST-STATUS");
        when(issue.getReporter().getDisplayName()).thenReturn("TEST_REPORTER");
        doReturn(issueType).when(issue).getFieldByName(ISSUE_TYPE_FIELD);
        doReturn(accessTimeLimit).when(issue).getFieldByName(ACCESS_TIME_LIMIT_FIELD);
        doReturn(approverField).when(issue).getFieldByName(APPROVER_FIELD);
        doReturn(startDateField).when(issue).getFieldByName(ACCESS_START_DATE_FIELD);
        doReturn(endDateField).when(issue).getFieldByName(ACCESS_END_DATE_FIELD);
    }

    private void assertions(List<AccessRequest> accessRequests) {
        assertEquals(expectedTestKey, accessRequests.get(0).getKey());
        assertEquals(expectedTestStatus, accessRequests.get(0).getStatus());
        assertEquals(expectedReporter, accessRequests.get(0).getReporter());
        assertEquals(expectedaccessTimeLimit, accessRequests.get(0).getAccessTimeLimit());
        assertEquals(expectedIssueType, accessRequests.get(0).getIssueType());
        assertEquals(expectedStartDate, accessRequests.get(0).getAccessStartDate());
        assertEquals(expectedEndDate, accessRequests.get(0).getAccessEndDate());
        assertEquals(expectedApprovers, accessRequests.get(0).getApprovers());
    }
}