package com.mscripts.appsec.yama.controllers;

import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.identitymanagement.model.UserDetail;
import com.mscripts.appsec.yama.services.impl.IAMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AWSController implements V1Controller{

    private final IAMService iamService;

    @Autowired
    public AWSController(IAMService iamService) {
        this.iamService = iamService;
    }

    @GetMapping("/aws/users")
    public ResponseEntity<List<UserDetail>> getUsers(){
        return ResponseEntity.of(iamService.getUsers());
    }


    @GetMapping("/aws/user/{userName}")
    public User getUsersDetails(@PathVariable String userName){
        return iamService.getUser(userName);
    }
}
