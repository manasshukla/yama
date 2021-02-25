package com.mscripts.appsec.yama.pollers.graylog;

import com.mscripts.appsec.yama.pollers.PollerInterface;
import com.mscripts.appsec.yama.services.impl.GraylogService;
import org.graylog2.rest.models.users.responses.UserSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class GraylogUsersPoller implements PollerInterface {

    GraylogService service;

    private static final Logger logger = LoggerFactory.getLogger(GraylogUsersPoller.class);

    public GraylogUsersPoller(GraylogService service) {
        this.service = service;
    }

    @Override
    public void performAudit() {
        startUserAccessReview();
    }

    public void startUserAccessReview() {
        Map<String, Set<UserSummary>> users = service.getUsers();
        printUsers(users);
    }

    private void printUsers(Map<String, Set<UserSummary>> allUsers) {
        allUsers.keySet().parallelStream().forEach(client -> {
            if (allUsers.get(client).isEmpty()) {
                logger.info("There are no unique users for {}", client);
            } else {
                logger.info("Current User provisioned for Client {}", client);
                logger.info("Username<------->FullName<------->Email<------->Roles<------->Last Activity");
                allUsers.get(client).parallelStream().forEach(user -> logger.info("{}<------->{}<------->{}<------->{}<------->{}",
                        user.fullName(), user.fullName(), user.email(), user.roles(), user.lastActivity()));
            }
        });
    }


}
