package com.mscripts.appsec.yama.model.jira;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@NoArgsConstructor @AllArgsConstructor @SuperBuilder
public @Data class AccessRequest {
    private String key;
    private String system;
    private String status;
    private String operation;
    private String reporter;
    private String requestedFor;
    private String accessTimeLimit;
    private String issueType;
    private List<String> approvers;
    private String accessStartDate;
    private String accessEndDate;
}
