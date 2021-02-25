package com.mscripts.appsec.yama.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jira")
public class JiraConfig extends CredentialConfig {

    private String url;

    public JiraConfig() {
    }

    public JiraConfig(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
