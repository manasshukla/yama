package com.mscripts.appsec.yama.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "graylog")
public class GraylogConfig {
    private Map<String, String> urls;
    private Map<String, String> ids;
    private Map<String, String> keys;


    public Map<String, String> getUrls() {
        return urls;
    }

    public Map<String, String> getKeys() {
        return keys;
    }

    public Map<String, String> getIds() { return ids; }

    public void setUrls(Map<String, String> urls) {
        this.urls = urls;
    }

    public void setKeys(Map<String, String> keys) {
        this.keys = keys;
    }

    public void setIds(Map<String, String> ids) { this.ids = ids; }
}
