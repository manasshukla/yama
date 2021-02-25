package com.mscripts.appsec.yama.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.emory.mathcs.backport.java.util.Collections;
import org.graylog2.rest.models.users.requests.Startpage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

public class GraylogUser {

    @Nonnull
    @JsonProperty
    public String username;

    @Nonnull
    @JsonProperty
    private final String email;

    @JsonProperty("full_name")
    private final String fullName;

    @Nonnull
    @JsonProperty
    private Set<String> roles = Collections.singleton(GRAYLOG_READER_ROLE);

    @JsonProperty
    private final List<String> permissions = Collections.singletonList("");

    @Nullable
    @JsonProperty
    private Startpage startpage;

    @Nullable
    @JsonProperty("timezone")
    private final String timeZone;

    @Nullable
    @JsonProperty("session_timeout_ms")
    private final Long sessionTimeOut;


    public GraylogUser(@Nonnull String username, @Nonnull String email, String fullName) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        sessionTimeOut = GRAYLOG_DEFAULT_SESSION_TIME_OUT;
        timeZone = GRAYLOG_DEFAULT_TIMEZONE;
    }

    public GraylogUser(@Nonnull String username, @Nonnull String email, String fullName, @Nonnull Set<String> roles) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
        sessionTimeOut = GRAYLOG_DEFAULT_SESSION_TIME_OUT;
        timeZone = GRAYLOG_DEFAULT_TIMEZONE;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraylogUser that = (GraylogUser) o;

        return username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return username.hashCode();
    }

    @Override
    public String toString() {
        return "GraylogUser{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", roles=" + roles +
                '}';
    }
}


