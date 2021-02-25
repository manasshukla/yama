package com.mscripts.appsec.yama.utils;

import com.mscripts.appsec.yama.model.AccessResponse;
import lombok.extern.slf4j.Slf4j;

import static com.mscripts.appsec.yama.constants.YamaConstants.DEPROVISION;
import static com.mscripts.appsec.yama.constants.YamaConstants.PROVISION;

@Slf4j
public class EmailUtil {

    private EmailUtil(){}

    public static AccessResponse sendDummyEmail(AccessResponse response) {
        switch (response.getOperation()) {
            case PROVISION:
                log.info("Sending email to {} with Password {}", response.getAccessRequest().getRequestedFor(), response.getSecret());
                break;
            case DEPROVISION:
                log.info("Access request has been removed for user {} as a part of AR {}", response.getAccessRequest().getRequestedFor(), response.getAccessRequest().getKey());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + response.getOperation());
        }
        return response;
    }
}
