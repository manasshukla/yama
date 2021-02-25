package com.mscripts.appsec.yama.scheduler;

import com.mscripts.appsec.yama.pollers.aws.AWSIAMPoller;
import com.mscripts.appsec.yama.pollers.graylog.GraylogUsersPoller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GraylogAuditScheduler {

    private final GraylogUsersPoller graylogPoller;
    private final AWSIAMPoller iamPoller;


    public GraylogAuditScheduler(GraylogUsersPoller poller, AWSIAMPoller iamPoller) {
        this.graylogPoller = poller;
        this.iamPoller = iamPoller;
    }


    @Scheduled(fixedDelayString = "${graylog.audit.frequency}")
    public void scheduleGraylogAudit() {
        log.info("Starting user audit job for graylog.");
        graylogPoller.performAudit();
        log.info("User Access review job completed.");
    }

    @Scheduled(fixedDelayString = "${iam.audit.frequency}")
    public void scheduleIAMAudit() throws InterruptedException {
        log.info("Starting IAM Audit");
        iamPoller.performAudit();
        log.info("IAM Audit completed.");
    }

}
