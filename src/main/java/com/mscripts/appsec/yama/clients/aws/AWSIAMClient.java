package com.mscripts.appsec.yama.clients.aws;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AWSIAMClient {

    AmazonIdentityManagement client;

    private static final Logger logger = LoggerFactory.getLogger(AWSIAMClient.class);

    private static final String LOG_STRING = "Method: {},  Operation: {}, Result: {}";
    private static final String LOG_STRING_EXCEPTION = "Method: {},  Operation: {}, Result: {} Reason: {}";
    private static final String LOG_STRING_STACKTRACE = "Method: {},  Operation: {}, Result: {} StackTrace: ";

    @Autowired
    public AWSIAMClient(AmazonIdentityManagement client) {
        this.client = client;
    }

    @Cacheable(value = "iam.users")
    public List<UserDetail> getUsers() {
        String methodName = "getUsers";
        List<UserDetail> userDetailList = new ArrayList<>();
        boolean complete;
        String marker = null;
        do {
            GetAccountAuthorizationDetailsRequest request = new GetAccountAuthorizationDetailsRequest().withMarker(marker);
            GetAccountAuthorizationDetailsResult accountAuthorizationDetails = client.getAccountAuthorizationDetails(request);
            marker = accountAuthorizationDetails.getMarker();
            complete = !accountAuthorizationDetails.isTruncated();
            log(methodName, null);
            userDetailList.addAll(accountAuthorizationDetails.getUserDetailList());
        } while (!complete);
        return userDetailList;
    }


    public User getUserDetails(String username) {
        GetUserRequest request = new GetUserRequest().withUserName(username);
        return client.getUser(request).getUser();
    }

    public boolean createUserWithLoginProfile(String userName, String secret) {
        return createUser(userName) && createLoginProfile(userName, secret);
    }

    public boolean deleteUserAndLoginProfile(String userName) {
        return deleteLoginProfile(userName) && deleteUser(userName);
    }

    public boolean createUser(String userName) {
        String methodName = "createUser";
        CreateUserRequest request = new CreateUserRequest().withUserName(userName);
        client.createUser(request);
        log(methodName, null);
        return true;
    }

    private boolean createLoginProfile(String userName, String secret) {
        String methodName = "createLoginProfile";
        CreateLoginProfileRequest request = new CreateLoginProfileRequest().withUserName(userName).withPassword(secret).withPasswordResetRequired(true);
        client.createLoginProfile(request);
        log(methodName, null);
        return true;
    }


    public boolean deleteUser(String userName) {
        String methodName = "deleteUser";
        DeleteUserRequest request = new DeleteUserRequest().withUserName(userName);
        client.deleteUser(request);
        log(methodName, null);
        return true;
    }

    private boolean deleteLoginProfile(String userName) {
        String methodName = "deleteLoginProfile";
        DeleteLoginProfileRequest request = new DeleteLoginProfileRequest().withUserName(userName);
        client.deleteLoginProfile(request);
        log(methodName, null);
        return true;
    }

    private void log(String methodName, AmazonIdentityManagementException exception) {

        if (null != exception) { //There is an exception
            logger.error(LOG_STRING_EXCEPTION, methodName, methodName, "FAILURE", exception.getLocalizedMessage());
            logger.debug(LOG_STRING_STACKTRACE, methodName, methodName, "FAILURE", exception);
        } else {
            logger.info(LOG_STRING, methodName, methodName, "SUCCESS");
        }
    }


}
