package com.mscripts.appsec.yama.pollers.aws;

import com.mscripts.appsec.yama.clients.aws.AWSIAMClient;
import com.mscripts.appsec.yama.pollers.PollerInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component @Slf4j
public class AWSIAMPoller implements PollerInterface {

    AWSIAMClient iamClient;

    public AWSIAMPoller(AWSIAMClient iamClient) {
        this.iamClient = iamClient;
    }

    private void startIAMAudit() {
        iamClient.getUsers().parallelStream().forEach(user -> log.info(user.toString()));
    }

    @Override
    public void performAudit() throws InterruptedException {
//        startIAMAudit();
//        iamClient.createUserWithLoginProfile("test-user-123");
//        Thread.sleep(20000);
//        iamClient.deleteUserAndLoginProfile("test-user-123");
    }
}
