package com.mscripts.appsec.yama.services.impl;

import com.jcabi.github.Github;
import com.jcabi.http.Request;
import com.jcabi.http.response.JsonResponse;
import com.mscripts.appsec.yama.model.AccessResponse;
import com.mscripts.appsec.yama.model.jira.AccessRequest;
import com.mscripts.appsec.yama.model.jira.GithubAccessRequest;
import com.mscripts.appsec.yama.services.ProvisioningService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.TeamService;
import org.eclipse.egit.github.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Service
@Slf4j
public class GithubService implements ProvisioningService {

    OrganizationService organizationService;
    UserService userService;
    TeamService teamService;
    Github github;

    private static final String LOG_STRING = "Method: {}, Event: {}::{}";

    @Autowired
    public GithubService(OrganizationService service, UserService userService, TeamService teamService, Github github) {
        this.organizationService = service;
        this.userService = userService;
        this.teamService = teamService;
        this.github = github;
    }

    public List<User> getUsers(String organisationName, Optional<OrganizationService.RoleFilter> role) throws IOException {
        String methodName = "getUsers";
        if (role.isEmpty()) {
            log.info(LOG_STRING, methodName, "GET_USER_FOR_ROLE", "default All");
            return organizationService.getMembers(organisationName, null);
        } else {
            log.info(LOG_STRING, methodName, "GET_USER_FOR_ROLE", role.get());
            return organizationService.getMembers(organisationName, role.get());
        }
    }

    public User getUser(String username) throws IOException {
        log.info(LOG_STRING, "getUser", "GETUSER", username);
        return userService.getUser(username);
    }

    public boolean removeUser(String organisation, String username) {
        log.info(LOG_STRING, "removeUser", "REMOVE_USER", username);
        try {
            organizationService.removeMember(organisation, username);
            return true;
        } catch (IOException e) {
            log.error("Exception occured while removing user from Github ", e);
            return false;
        }
    }


    public List<Team> getTeams(String organisation) throws IOException {
        log.info(LOG_STRING, "getTeams", "GET_TEAMS_FOR_ORG", organisation);
        return teamService.getTeams(organisation);
    }

    public boolean addUser(String organisation, String teamslug, String username) {
        String uri = ORGS_TAG + organisation +
                TEAMS_TAG + teamslug +
                MEMBERSHIPS_TAG + username;


        try {
            String body = github.entry().uri()
                    .path(uri).back().method(Request.PUT)
                    .fetch()
                    .as(JsonResponse.class)
                    .body();

            log.info("Invite send successfully. Response: {}", body);
            return true;
        } catch (IOException e) {
            log.error("Exception occured while adding user to github", e);
            return false;
        }
    }


    @Override
    public AccessResponse provisionUser(AccessRequest request) {
        log.info(LOG_STRING, "provisionUser", "ADD_USER", ((GithubAccessRequest) request).getUsername());
        log.info("ORG: {}, teams: {}", ((GithubAccessRequest) request).getOrganisation(), StringUtils.collectionToCommaDelimitedString(((GithubAccessRequest) request).getTeams()));
        String username = ((GithubAccessRequest) request).getUsername();
        String organisation = ((GithubAccessRequest) request).getOrganisation();
        List<String> teamSlugs = ((GithubAccessRequest) request).getTeams();

        AccessResponse response = new AccessResponse(request);
        response.setOperation(PROVISION);

        var failedTeams = teamSlugs.stream()
                .filter(team  -> !addUser(organisation, team, username))
                .collect(Collectors.toList());

        if(failedTeams.isEmpty()){
            response.setResult(SUCCESS);
        }else{
            response.setResult(ERROR);
            response.setErrorReason("Failed to provision user for teams : "+ StringUtils.collectionToCommaDelimitedString(failedTeams));
        }

        return response;

    }

    @Override
    public AccessResponse deprovisionUser(AccessRequest request) {
        log.info(LOG_STRING, "deprovisionUser", "REMOVE_USER", ((GithubAccessRequest) request).getUsername());
        String username = ((GithubAccessRequest) request).getUsername();
        String organisation = ((GithubAccessRequest) request).getOrganisation();

        AccessResponse response = new AccessResponse(request);
        response.setOperation(DEPROVISION);

        try {
            organizationService.removeMember(organisation, username);
            response.setResult(SUCCESS);
        } catch (IOException e) {
            log.error("Exception occured while removing user from Github ", e);
            response.setResult(ERROR);
            response.setErrorReason(e.getLocalizedMessage());
        }

        return response;

    }
}
