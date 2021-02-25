package com.mscripts.appsec.yama.model.jira;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@SuperBuilder @Value
public class GraylogAccessRequest extends AccessRequest{
    private List<String> graylogClients;
    private Set<String> graylogRoles;

    @Override
    public String toString() {
        return "GraylogAccessRequest{" +
                super.toString()+", "+
                "graylogClients=" + graylogClients +
                ", graylogRoles=" + graylogRoles +
                '}';
    }
}
