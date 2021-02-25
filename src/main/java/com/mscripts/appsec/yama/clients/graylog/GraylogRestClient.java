package com.mscripts.appsec.yama.clients.graylog;

import com.google.common.base.Strings;
import com.mscripts.appsec.yama.exception.ValidationException;
import com.mscripts.appsec.yama.model.graylog.requests.ChangePasswordRequest;
import com.mscripts.appsec.yama.model.graylog.requests.CreateUserRequest;
import lombok.extern.slf4j.Slf4j;
import org.graylog2.rest.models.users.responses.UserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Component
@Slf4j
public class GraylogRestClient {


    private final RestTemplateBuilder restTemplateBuilder;

    @Autowired
    public GraylogRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public UserList getUserList(String url, String id, String key) {
        RestTemplate restTemplate = getRestTemplate(id, key);
        UserList userList = null;
        try {
            userList = restTemplate.getForObject(url, UserList.class);
        } catch (RestClientException e) {
            log.error("Exception occurred while connecting to {}", url, e);
        }

        return userList;
    }

    public boolean createUser(String url, String id, String key, CreateUserRequest user) {
        RestTemplate restTemplate = getRestTemplate(id, key);
        try {
            ResponseEntity<CreateUserRequest> responseEntity = restTemplate.postForEntity(url, user, CreateUserRequest.class);
            log.info("Crete user at {}, response code {}, response body {}", url, responseEntity.getStatusCode(), responseEntity.getBody());
            return (responseEntity.getStatusCodeValue() == 201);
        } catch (RestClientException exception) {
            log.error("Exception occured while creating graylog user for {}. Exception : ", url, exception);
            return false;
        }
    }

    public boolean deleteUser(String url, String id, String key, String userName) {
        if (Strings.isNullOrEmpty(userName)) {
            throw new ValidationException("Username could not be null in delete request");
        }

        RestTemplate restTemplate = getRestTemplate(id, key);
        try {
            restTemplate.delete(url + "/" + userName);
            ResponseEntity<String> responseEntity = restTemplate.exchange(url + "/" + userName, HttpMethod.DELETE, null, String.class);
            log.info("Delete user at {}, response code {}, response body {}", url, responseEntity.getStatusCodeValue(), responseEntity.getBody());
            return (responseEntity.getStatusCodeValue() == 204);
        } catch (RestClientException exception) {
            log.error("Exception occured while deleting graylog user for {}. Exception : ", url, exception);
            return false;
        }
    }

    public boolean changeUserPassword(String url, String id, String key, String userName, String oldPassword, String newPassword) {
        RestTemplate restTemplate = getRestTemplate(id, key);
        String uriString = url + "/" + userName + "/password";
        ChangePasswordRequest request = ChangePasswordRequest.build(oldPassword, newPassword);
        restTemplate.put(uriString, request, ChangePasswordRequest.class);
        return true;
    }

    private RestTemplate getRestTemplate(String accessId, String accessKey) {
        return restTemplateBuilder.basicAuthentication(accessId, accessKey)
                .build();
    }


}
