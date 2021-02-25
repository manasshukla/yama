package com.mscripts.appsec.yama.model;

import com.mscripts.appsec.yama.model.jira.AccessRequest;
import lombok.Data;

@Data
public class AccessResponse {
    private AccessRequest accessRequest;
    private String operation;
    private String secret;
    private String result;
    private boolean emailSent;
    private String errorReason;

    public AccessResponse(AccessRequest accessRequest) {
        this.accessRequest = accessRequest;
    }

    public AccessResponse(AccessRequest accessRequest, String secret) {
        this.accessRequest = accessRequest;
        this.secret = secret;
    }
}