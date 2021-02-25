package com.mscripts.appsec.yama.clients.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.exception.ValidationException;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.model.jira.GithubAccessRequest;
import com.mscripts.appsec.yama.model.jira.GraylogAccessRequest;
import com.mscripts.appsec.yama.utils.YamaUtils;
import edu.emory.mathcs.backport.java.util.Collections;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Component
@Slf4j
public class JiraClient {


    public static final String DISPLAY_NAME = "displayName";
    private final JiraRestClient restClient;


    @Autowired
    public JiraClient(JiraRestClient restClient) {
        this.restClient = restClient;
    }

    public List<AccessRequest> getAccessRequests(YSystem system, String state) {
        switch (system) {
            case GRAYLOG:
                return processGraylogAccessRequests(state);
            case GITHUB:
                return processGithubAccessRequests(state);
            case AWS:
                return processAWSAccessRequests(state);
            default:
                throw new ValidationException("This system is not supported");
        }
    }

    private List<AccessRequest> processAWSAccessRequests(String state) {
        Iterable<Issue> awsTickets = search(YSystem.AWS, state);
        ArrayList<AccessRequest> awsRequests = new ArrayList<>();
        for (Issue awsTicket : awsTickets) {
            AccessRequest accessRequest = parseAWSRequests(awsTicket);
            log.debug("AWS Access Request POJO::{}", accessRequest);
            awsRequests.add(accessRequest);
        }
        return awsRequests;
    }

    private AccessRequest parseAWSRequests(Issue issue) {
        AccessRequest awsRequests = AccessRequest.builder().system(AWS).build();
        return populateAccessRequestPOJO(awsRequests, issue);
    }

    private List<AccessRequest> processGraylogAccessRequests(String state) {
        Iterable<Issue> graylogTickets = search(YSystem.GRAYLOG, state);
        ArrayList<AccessRequest> graylogRequests = new ArrayList<>();
        for (Issue graylogTicket : graylogTickets) {
            AccessRequest accessRequest = parseGraylogTicketToPOJO(graylogTicket);
            log.debug("Graylog Access Request POJO::{}", accessRequest);
            graylogRequests.add(accessRequest);
        }
        return graylogRequests;
    }

    private AccessRequest parseGraylogTicketToPOJO(Issue issue) {

        List<String> graylogClients = getSelectedValues(issue.getFieldByName(GRAYLOG_CLIENTS_FIELD));
        Set<String> graylogRoles = new HashSet<>(getSelectedValues(issue.getFieldByName(GRAYLOG_ROLE_FIELD)));

        GraylogAccessRequest graylogRequest = GraylogAccessRequest.builder()
                .system(GRAYLOG)
                .graylogClients(graylogClients)
                .graylogRoles(graylogRoles)
                .build();
        return populateAccessRequestPOJO(graylogRequest, issue);
    }

    private List<AccessRequest> processGithubAccessRequests(String state) {
        Iterable<Issue> githubTickets = search(YSystem.GITHUB, state);
        ArrayList<AccessRequest> githubAccessRequets = new ArrayList<>();
        for (Issue githubTicket : githubTickets) {
            AccessRequest accessRequest = parseGithubTicketToPOJO(githubTicket);
            log.info("Github Access Request POJO::{}", accessRequest);
            githubAccessRequets.add(accessRequest);
        }
        return githubAccessRequets;
    }

    private AccessRequest parseGithubTicketToPOJO(Issue issue) {
        String githubOrg = getSelectedValue(issue.getFieldByName(GITHUB_ORG_FIELD));
        String githubUserName = issue.getFieldByName(GITHUB_USER_NAME_FIELD).getValue().toString();
        List<String> githubTeams = getSelectedValues(issue.getFieldByName(GITHUB_TEAMS_FIELD));

        GithubAccessRequest githubRequests = GithubAccessRequest.builder()
                .system(GITHUB)
                .organisation(githubOrg).teams(githubTeams).username(githubUserName)
                .build();
        return populateAccessRequestPOJO(githubRequests, issue);
    }

    private Iterable<Issue> search(YSystem system, String state) {
        String query = YamaUtils.getJQLQuery(system, state);
        SearchResult result = restClient.getSearchClient().searchJql(query).claim();
        return result.getIssues();
    }

    private AccessRequest populateAccessRequestPOJO(AccessRequest accessRequest, Issue issue) {
        var key = issue.getKey();
        var status = issue.getStatus().getName();
        var reporter = issue.getReporter().getDisplayName();
        Optional<Object> requestedFor = Optional.ofNullable(issue.getFieldByName(REQUESTED_FOR_FIELD).getValue());
        var accessTimeLimit = getSelectedValue(issue.getFieldByName(ACCESS_TIME_LIMIT_FIELD));
        var issueType = getSelectedValue(issue.getFieldByName(ISSUE_TYPE_FIELD));
        var approvers = getSelectedValues(issue.getFieldByName(APPROVER_FIELD), DISPLAY_NAME);

//        //If requestedFor field is Empty, treat the reported as the requester
        if (requestedFor.isEmpty()) {
            requestedFor = Optional.of(reporter);
        }

        accessRequest.setKey(key);
        accessRequest.setStatus(status);
        accessRequest.setReporter(reporter);
        accessRequest.setRequestedFor(String.valueOf(requestedFor.get()));
        accessRequest.setAccessTimeLimit(accessTimeLimit);
        accessRequest.setIssueType(issueType);
        accessRequest.setApprovers(approvers);

        //If the access is temporary, get the start and end dates
        if (accessTimeLimit.equals("Temporary")) {
            var startDate = issue.getFieldByName(ACCESS_START_DATE_FIELD).getValue().toString();
            var endDate = issue.getFieldByName(ACCESS_END_DATE_FIELD).getValue().toString();
            accessRequest.setAccessStartDate(startDate);
            accessRequest.setAccessEndDate(endDate);
        }

        return accessRequest;
    }

    /**
     * @param field JiraIsssuefield
     * @return this method is the default version which returns the selected options from a checkbox or multiple select field.
     * This method parses the json to get values from the "value" key in the json.
     */
    private List<String> getSelectedValues(IssueField field) {
        return getSelectedValues(field, "value");
    }

    /**
     * @param field JiraIsssuefield
     * @param key   to be selected from json
     * @return this method is the customised version which returns the values of selected checkboxes or multiple select fields.
     * This method parses the json to get values for the given key
     */
    private List<String> getSelectedValues(IssueField field, String key) {
        if (null == field.getValue()) {
            return Collections.emptyList();
        }
        JSONArray jsonArray = new JSONArray(field.getValue().toString());
        return IntStream.range(0, jsonArray.length())
                .mapToObj(index -> ((JSONObject) jsonArray.get(index)).optString(key))
                .collect(Collectors.toList());
    }

    /**
     * @param field JiraIsssuefield
     * @return this method is the default version which returns the value corresponding to the key "value" from a json
     */
    private String getSelectedValue(IssueField field) {
        return getSelectedValue(field, "value");
    }

    /**
     * @param field JiraIsssuefield
     * @param key   to be selected from json
     * @return this method is the customised version which returns the value corresponding to a given key from a json
     */
    private String getSelectedValue(IssueField field, String key) {
        if (null == field.getValue()) {
            return StringUtil.EMPTY_STRING;
        }
        return new JSONObject(field.getValue().toString()).optString(key);
    }


    public void transitionIssue(String issueKey, int transitionId) {
        Issue issue = restClient.getIssueClient().getIssue(issueKey).claim();
        TransitionInput transitionInput = new TransitionInput(transitionId, null, getComment(transitionId));
        restClient.getIssueClient().transition(issue.getTransitionsUri(), transitionInput).claim();

    }

    private static Comment getComment(int transitionId) {
        if (transitionId == PROVISIONED_TRANSITION_ID) {
            return Comment.valueOf("Provisioned by Yama App");
        } else if (transitionId == DEPROVISIONED_TRANSITION_ID) {
            return Comment.valueOf("Deprovisioned by Yama App");
        } else {
            throw new ValidationException("Not a valid transition id");
        }
    }

}
