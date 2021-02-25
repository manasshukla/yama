package com.mscripts.appsec.yama.services;

import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import org.springframework.stereotype.Service;

@Service
public interface ProvisioningService {
    AccessResponse provisionUser(AccessRequest request);
    AccessResponse deprovisionUser(AccessRequest request);
}
