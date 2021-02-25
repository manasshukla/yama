package com.mscripts.appsec.yama.services.impl;

import com.google.common.collect.Sets;
import com.mscripts.appsec.yama.clients.graylog.GraylogRestClient;
import com.mscripts.appsec.yama.config.GraylogConfig;
import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.model.graylog.requests.CreateUserRequest;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.model.jira.GraylogAccessRequest;
import com.mscripts.appsec.yama.services.ProvisioningService;
import com.mscripts.appsec.yama.utils.YamaUtils;
import lombok.extern.slf4j.Slf4j;
import org.graylog2.rest.models.users.responses.UserList;
import org.graylog2.rest.models.users.responses.UserSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Service
@Slf4j
public class GraylogService implements ProvisioningService {

    private final Map<String, String> urls;
    private final Map<String, String> accessKeys;
    private final Map<String, String> accessIds;
    private final GraylogRestClient graylogClient;

    private static final String LOG_STRING = "Method: {},  Event: {}";


    @Autowired
    public GraylogService(GraylogConfig graylogConfig, GraylogRestClient graylogClient) {
        this.graylogClient = graylogClient;
        this.urls = graylogConfig.getUrls();
        this.accessIds = graylogConfig.getKeys();
        this.accessKeys = graylogConfig.getKeys();
    }


    public Map<String, Set<UserSummary>> getUsers() {
        HashMap<String, Set<UserSummary>> allUsers = new HashMap<>();
        log.info(LOG_STRING, "getUsers", "Processing graylog users across all graylog instances");
        urls.keySet().parallelStream().forEach(client -> {
            UserList userList = graylogClient.getUserList(urls.get(client), accessIds.get(client), accessKeys.get(client));
            Set<UserSummary> users = parseToSet(userList);
            allUsers.put(client, users);
        });
        return segregateCommonAndUniqueUsers(allUsers);
    }

    private Set<UserSummary> parseToSet(UserList userList) {
        Set<UserSummary> userSet = Collections.emptySet();
        log.info(LOG_STRING, "parseToSet", "Convert the Graylog UserList object to a set of User summaries");
        if (Objects.nonNull(userList)) {
            userSet = userList.users().parallelStream().collect(Collectors.toSet());
        }
        return userSet;
    }

    private Map<String, Set<UserSummary>> segregateCommonAndUniqueUsers(Map<String, Set<UserSummary>> allUsers) {
        log.info(LOG_STRING, "segregateCommonAndUniqueUsers", "Go through the list of all users and identify users common across all graylog instances");
        Set<UserSummary> commonUsers = getCommonUsers(allUsers);
        allUsers.keySet().parallelStream().forEach(graylogInstance -> {
            Set<UserSummary> uniqueGraylogUsers = allUsers.get(graylogInstance);
            Set<UserSummary> difference = Sets.difference(uniqueGraylogUsers, commonUsers);
            allUsers.put(graylogInstance, difference);
        });
        allUsers.put("common", commonUsers);
        return allUsers;

    }

    private Set<UserSummary> getCommonUsers(Map<String, Set<UserSummary>> allUsers) {
        Optional<Set<UserSummary>> optionalUserSummaries = allUsers.values().parallelStream().reduce(Sets::intersection);
        return optionalUserSummaries.orElse(Collections.emptySet());
    }

    public Optional<UserList> getUsers(String pharmPrefix) {
        String pharmURL = urls.get(pharmPrefix);
        String pharmId = accessIds.get(pharmPrefix);
        String pharmKey = accessKeys.get(pharmPrefix);
        return Optional.ofNullable(graylogClient.getUserList(pharmURL, pharmId, pharmKey));
    }

    public boolean addUser(String pharmPrefix, CreateUserRequest user) {
        return graylogClient.createUser(urls.get(pharmPrefix), accessIds.get(pharmPrefix), accessKeys.get(pharmPrefix), user);
    }

    public boolean removeUser(String pharmPrefix, String username) {
        return graylogClient.deleteUser(urls.get(pharmPrefix), accessIds.get(pharmPrefix), accessKeys.get(pharmPrefix), username);
    }

    @Override
    public AccessResponse provisionUser(AccessRequest request) {
        //Setup and extraction
        GraylogAccessRequest graylogAccessRequest = (GraylogAccessRequest) request;
        List<String> graylogClients = graylogAccessRequest.getGraylogClients();
        Set<String> graylogRoles = graylogAccessRequest.getGraylogRoles();
        String secret = YamaUtils.createComplexPassword(PASSWORD_LENGTH);
        CreateUserRequest userRequest = CreateUserRequest.build(request.getRequestedFor(),
                request.getRequestedFor(), request.getRequestedFor(), secret, graylogRoles);

        AccessResponse response = new AccessResponse(request, secret);
        response.setOperation(PROVISION);

        //Provisioning logic
        List<String> failedPharmacies = graylogClients.stream().filter(pharmacy -> !addUser(pharmacy, userRequest))
                .collect(Collectors.toList());

        if (failedPharmacies.isEmpty()) {
            response.setResult(SUCCESS);
        } else {
            response.setResult(ERROR);
            response.setErrorReason("Failed to provision user for pharmacies : " + StringUtils.collectionToCommaDelimitedString(failedPharmacies));
        }

        return response;
    }

    @Override
    public AccessResponse deprovisionUser(AccessRequest request) {
        GraylogAccessRequest graylogAccessRequest = (GraylogAccessRequest) request;
        List<String> graylogClients = graylogAccessRequest.getGraylogClients();
        String username = graylogAccessRequest.getRequestedFor();

        AccessResponse response = new AccessResponse(request);
        response.setOperation(DEPROVISION);

        //Deprovisioning logic
        List<String> failedPharmacies = graylogClients.stream().filter(pharmacy -> !removeUser(pharmacy, username))
                .collect(Collectors.toList());

        if (failedPharmacies.isEmpty()) {
            response.setResult(SUCCESS);
        } else {
            response.setResult(ERROR);
            response.setErrorReason("Failed to provision user for pharmacies : " + StringUtils.collectionToCommaDelimitedString(failedPharmacies));
        }

        return response;

    }
}
