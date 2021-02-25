package com.mscripts.appsec.yama.services.impl;

import com.amazonaws.services.identitymanagement.model.AmazonIdentityManagementException;
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.identitymanagement.model.UserDetail;
import com.mscripts.appsec.yama.clients.aws.AWSIAMClient;
import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.services.ProvisioningService;
import com.mscripts.appsec.yama.utils.YamaUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Service
@Slf4j
public class IAMService implements ProvisioningService {

    private final AWSIAMClient client;

    public IAMService(AWSIAMClient client) {
        this.client = client;
    }

    public Optional<List<UserDetail>> getUsers() {
        List<UserDetail> result = client.getUsers();
        return Optional.ofNullable(result);
    }

    public User getUser(String userName) {
        return client.getUserDetails(userName);
    }

    @Override
    public AccessResponse provisionUser(AccessRequest request) {
        String secret = YamaUtils.createComplexPassword(PASSWORD_LENGTH);
        AccessResponse response = new AccessResponse(request, secret);
        response.setOperation(PROVISION);
        try {
            client.createUserWithLoginProfile(request.getRequestedFor(), secret);
            response.setResult(SUCCESS);
        } catch (AmazonIdentityManagementException exception) {
            log.error("Exception while provisioning user from AWS", exception);
            response.setResult(ERROR);
            response.setErrorReason(exception.getMessage());
        }
        return response;
    }

    @Override
    public AccessResponse deprovisionUser(AccessRequest request) {
        AccessResponse response = new AccessResponse(request);
        response.setOperation(DEPROVISION);
        try {
            client.deleteUserAndLoginProfile(request.getRequestedFor());
            response.setResult(SUCCESS);
        } catch (AmazonIdentityManagementException exception) {
            log.error("Exception while deprovisioning user from AWS", exception);
            response.setResult(ERROR);
            response.setErrorReason(exception.getMessage());
        }
        return response;
    }
}
