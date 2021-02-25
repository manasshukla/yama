package com.mscripts.appsec.yama.constants;

public class YamaConstants {
    public static final int CONVERSION_FACTOR_FOR_MINUTES = 60 * 1000;
    public static final int CONVERSION_FACTOR_FOR_HOURS = 3600 * 1000;
    public static final int MIN_PASSWORD_LENGTH_PER_POLICY = 10;
    public static final int PASSWORD_LENGTH = 14;
    public static final long GRAYLOG_DEFAULT_SESSION_TIME_OUT = 3600000;
    public static final String GRAYLOG_DEFAULT_TIMEZONE = "UTC";
    public static final String GRAYLOG_READER_ROLE = "Reader";
    public static final String GRAYLOG_ADMIN_ROLE = "Admin";
    public static final String YAMA_APP = "yama-application";
    public static final String ORGS_TAG = "/orgs/";
    public static final String TEAMS_TAG = "/teams/";
    public static final String MEMBERSHIPS_TAG = "/memberships/";

    private static final String JIRA_PROJECT_NAME = "AR";
    public static final String GITHUB = "GITHUB";
    public static final String GRAYLOG = "GRAYLOG";
    public static final String AWS = "AWS";
    public static final String APPROVED = "APPROVED";
    public static final String PROVISIONED = "PROVISIONED";
    public static final String DEPROVISIONED = "DEPROVISIONED";
    public static final String PROVISION = "PROVISION";
    public static final String DEPROVISION = "DEPROVISION";
    public static final String TIMEBOUND_DEPROVISION = "TIMEBOUND_DEPROVISION";
    public static final String REPORTER_FIELD = "Reporter";
    public static final String REQUESTED_FOR_FIELD = "Access Requested on Behalf";
    public static final String APPROVER_FIELD = "Approvers";

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";


    public static final String GITHUB_ORG_FIELD = "github_organisation";
    public static final String GITHUB_USER_NAME_FIELD = "github_user_name";
    public static final String GITHUB_TEAMS_FIELD = "github_teams";
    public static final String ACCESS_START_DATE_FIELD = "access_start_date";
    public static final String ACCESS_END_DATE_FIELD = "access_end_date";
    public static final String ACCESS_TIME_LIMIT_FIELD = "Access Time Limit";
    public static final String ISSUE_TYPE_FIELD = "Issue Type";
    public static final String GRAYLOG_CLIENTS_FIELD = "graylog_clients";
    public static final String GRAYLOG_ROLE_FIELD = "graylog_roles";

    public static final int PROVISIONED_TRANSITION_ID = 21;
    public static final int DEPROVISIONED_TRANSITION_ID = 41;
    private static final String ISSUE_TYPE_ADD_ACCESS = "Add Access";
    private static final String ISSUE_TYPE_REMOVE_ACCESS = "Remove Access";
    public static final String PROVISIONING_JQL = "project = " + JIRA_PROJECT_NAME + " AND SYSTEM = {0} AND status = APPROVED AND \"ISSUE TYPE\"=\"" + ISSUE_TYPE_ADD_ACCESS + "\"";

    public static final String DEPROVISIONING_JQL = "project=" + JIRA_PROJECT_NAME + " AND SYSTEM = {0} AND \"Issue Type[Dropdown]\" = \"" + ISSUE_TYPE_REMOVE_ACCESS + "\" AND  status in (APPROVED, PROVISIONED)";

    public static final String TIMEBOUND_DEPROVISIONING_JQL = "project=" + JIRA_PROJECT_NAME + " AND SYSTEM = {0} AND status=PROVISIONED AND \"Access Time Limit[Radio Buttons]\" = Temporary AND \"access_end_date[Date]\" = endOfDay()";


    private YamaConstants() {
    }

}
