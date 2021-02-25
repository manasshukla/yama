package com.mscripts.appsec.yama;

import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

import static com.mscripts.appsec.yama.TestManager.TEST_USERNAME;
import static com.mscripts.appsec.yama.constants.YamaConstants.*;

public class JiraTestManager {
    private static final String githubOrgString = "{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10027\",\"value\":\"TESTGITHUBORG\",\"id\":\"10027\"}";
    private static final String githubTeamsString = "[{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10029\",\"value\":\"TESTGITHUBTEAM1\",\"id\":\"10029\"},{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10030\",\"value\":\"TESTGITHUBTEAM2\",\"id\":\"10030\"}]";
    private static final String graylogClientString = "[{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10020\",\"value\":\"GL Client 1\",\"id\":\"10020\"},{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10021\",\"value\":\"GL Client 2\",\"id\":\"10021\"}]";
    private static final String graylogRoleString = "[{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10020\",\"value\":\"GL Role 1\",\"id\":\"10020\"},{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10021\",\"value\":\"GL Role 2\",\"id\":\"10021\"}]";
    private static final String approvers = "[{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/user?accountId=5e4d395e2110470c8da1f8ee\",\"accountId\":\"5e4d395e2110470c8da1f8ee\",\"emailAddress\":\"manas.shukla@cardinalhealth.com\",\"avatarUrls\":{\"48x48\":\"https:\\/\\/avatar-management--avatars.us-west-2.prod.public.atl-paas.net\\/5e4d395e2110470c8da1f8ee\\/d863500b-835a-49e2-93a9-0fe408795183\\/48\",\"24x24\":\"https:\\/\\/avatar-management--avatars.us-west-2.prod.public.atl-paas.net\\/5e4d395e2110470c8da1f8ee\\/d863500b-835a-49e2-93a9-0fe408795183\\/24\",\"16x16\":\"https:\\/\\/avatar-management--avatars.us-west-2.prod.public.atl-paas.net\\/5e4d395e2110470c8da1f8ee\\/d863500b-835a-49e2-93a9-0fe408795183\\/16\",\"32x32\":\"https:\\/\\/avatar-management--avatars.us-west-2.prod.public.atl-paas.net\\/5e4d395e2110470c8da1f8ee\\/d863500b-835a-49e2-93a9-0fe408795183\\/32\"},\"displayName\":\"test.displayName\",\"active\":true,\"timeZone\":\"Asia\\/Calcutta\",\"accountType\":\"atlassian\"}]";
    private static final String requestedForString = "{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10031\",\"value\":\"TEST_REQUESTED_FOR\",\"id\":\"10031\"}";
    private static final String accessTimeLimitString = "{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10031\",\"value\":\"Temporary\",\"id\":\"10031\"}";
    private static final String issueTypeString = "{\"self\":\"https:\\/\\/localhost\\/rest\\/api\\/2\\/customFieldOption\\/10031\",\"value\":\"Add Access\",\"id\":\"10031\"}";


    //Generic JIRA Fields:
    public static final String expectedTestKey = "TEST-KEY";
    public static final String expectedTestStatus = "TEST-STATUS";
    public static final String expectedStartDate = "testStartDate";
    public static final String expectedEndDate = "testEndDate";
    public static final String expectedGithubUserName = TEST_USERNAME;
    public static final String expectedRequestedFor = "TEST_REQUESTED_FOR";
    public static final String expectedReporter = "TEST_REPORTER";
    public static final String expectedaccessTimeLimit = "Temporary";
    public static final String expectedIssueType = "Add Access";
    public static final String expectedGithubOrg = "TESTGITHUBORG";
    public static final List<String> expectedGithubTeams = ImmutableList.of("TESTGITHUBTEAM1", "TESTGITHUBTEAM2");
    public static final List<String> expectedGLClients = ImmutableList.of("GL Client 1", "GL Client 2");
    public static final Set<String> expectedGLRoles = ImmutableSet.of("GL Role 1", "GL Role 2");

    public static final List<String> expectedApprovers = ImmutableList.of("test.displayName");
    public static final String issueId = "TEST-ISSUE-ID";
    public static final IssueField requestedFor = new IssueField(issueId, REQUESTED_FOR_FIELD, null, requestedForString);
    public static final IssueField nullRequestedFor = new IssueField(issueId, REQUESTED_FOR_FIELD, null, null);
    public static final IssueField accessTimeLimit = new IssueField(issueId, ACCESS_TIME_LIMIT_FIELD, null, accessTimeLimitString);
    public static final IssueField issueType = new IssueField(issueId, ISSUE_TYPE_FIELD, null, issueTypeString);
    public static final IssueField approverField = new IssueField(issueId, APPROVER_FIELD, null, approvers);
    public static final IssueField startDateField = new IssueField(issueId, ACCESS_START_DATE_FIELD, null, expectedStartDate);
    public static final IssueField endDateField = new IssueField(issueId, ACCESS_END_DATE_FIELD, null, expectedEndDate);
    public static final IssueField githubOrg = new IssueField(issueId, GITHUB_ORG_FIELD, null, githubOrgString);
    public static final IssueField githubTeams = new IssueField(issueId, GITHUB_TEAMS_FIELD, null, githubTeamsString);
    public static final IssueField githubUser = new IssueField(issueId, GITHUB_USER_NAME_FIELD, null, TEST_USERNAME);
    public static final IssueField graylogClients = new IssueField(issueId, GRAYLOG_CLIENTS_FIELD, null, graylogClientString);
    public static final IssueField graylogRoles = new IssueField(issueId, GRAYLOG_CLIENTS_FIELD, null, graylogRoleString);


}
