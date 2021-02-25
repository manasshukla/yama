package com.mscripts.appsec.yama.model.graylog.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mscripts.appsec.yama.model.GraylogUser;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class CreateUserRequest extends GraylogUser {

    @JsonProperty
    private final String password;

    private CreateUserRequest(String username, String email, String fullName, String password, Set<String> roles) {
        super(username, email, fullName, roles);
        this.password = password;
    }

    public static CreateUserRequest build(@NotNull String username, @NotNull String email, String fullName, @NotNull String password, @NotNull Set<String> roles) {
        return new CreateUserRequest(username, email, fullName, password, roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateUserRequest)) return false;
        if (!super.equals(o)) return false;

        CreateUserRequest that = (CreateUserRequest) o;

        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }
}
