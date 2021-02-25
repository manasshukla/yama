package com.mscripts.appsec.yama.controllers;

import com.mscripts.appsec.yama.services.impl.GraylogService;
import org.graylog2.rest.models.users.responses.UserList;
import org.graylog2.rest.models.users.responses.UserSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
public class GraylogController implements V1Controller{

    private final GraylogService service;

    public GraylogController(GraylogService service) {
        this.service = service;
    }

    @GetMapping("/graylog/users")
    public Map<String, Set<UserSummary>> getAllUsers(){
        return service.getUsers();
    }

    @GetMapping("/graylog/users/{pharmPrefix}")
    public ResponseEntity<UserList> getUsers(@PathVariable String pharmPrefix){
        return ResponseEntity.of(service.getUsers(pharmPrefix));
    }

}
