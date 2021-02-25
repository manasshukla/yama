package com.mscripts.appsec.yama.controllers;


import com.mscripts.appsec.yama.services.ProvisioningService;
import com.mscripts.appsec.yama.services.impl.GithubService;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService.RoleFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class GithubController implements V1Controller {

    GithubService service;
    ProvisioningService provisioningService;

    private static final String GITHUB = "/github";

    @Autowired
    public GithubController(GithubService service) {
        this.service = service;
    }

    public GithubController(GithubService service, ProvisioningService provisioningService) {
        this.service = service;
        this.provisioningService = provisioningService;
    }

    @GetMapping(GITHUB + "/org/{organisationName}/users")
    public List<User> getUsers(@PathVariable String organisationName, @RequestParam Optional<RoleFilter> role) throws IOException {
        return service.getUsers(organisationName, role);
    }

    @GetMapping(GITHUB + "/user/{username}")
    public User getUser(@PathVariable String username) throws IOException {
        return service.getUser(username);
    }

    @GetMapping(GITHUB + "/team/{orgname}")
    public List<Team> getTeams(@PathVariable String orgname) throws IOException {
        return service.getTeams(orgname);
    }
}
