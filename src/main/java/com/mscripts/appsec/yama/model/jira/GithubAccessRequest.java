package com.mscripts.appsec.yama.model.jira;

import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder @Value
public class GithubAccessRequest extends AccessRequest{
    private String username;
    private String organisation;
    private List<String> teams;

    @Override
    public String toString() {
        return "GithubAccessRequest{" +
                super.toString()+", "+
                "username='" + username + '\'' +
                ", organisation='" + organisation + '\'' +
                ", teams=" + teams +
                '}';
    }
}
