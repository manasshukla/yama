package com.mscripts.appsec.yama.model.graylog.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;

import javax.annotation.Nonnull;

public class ChangePasswordRequest {

    @NonNull
    @JsonProperty("old_password")
    private final String oldPassword;

    @JsonProperty
    private final String password;

    private ChangePasswordRequest(String oldPassword, String password) {
        this.oldPassword = oldPassword;
        this.password = password;
    }


    public static ChangePasswordRequest build(@Nonnull String oldPassword, @Nonnull String password) {
        return new ChangePasswordRequest(oldPassword, password);
    }
}
