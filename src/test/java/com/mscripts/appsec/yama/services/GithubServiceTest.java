package com.mscripts.appsec.yama.services;

import com.google.common.collect.Iterables;
import com.mscripts.appsec.yama.TestManager;
import com.mscripts.appsec.yama.services.impl.GithubService;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.TeamService;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.mscripts.appsec.yama.TestManager.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GithubServiceTest {

    @Mock
    OrganizationService orgService;

    @Mock
    UserService userService;

    @Mock
    TeamService teamService;


    @InjectMocks
    GithubService sut;

    GithubServiceTest() {
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void testGetUsers() throws IOException {
        //Mock
        when(orgService.getMembers(anyString(), eq(null))).thenReturn(Collections.singletonList(TestManager.getTestGithubUser()));

        //Execute
        List<User> actual = sut.getUsers(TEST_ORGANISATION, Optional.empty());

        //Assert
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(TEST_FULL_NAME, actual.get(0).getName());
        assertEquals(TEST_EMAIL, actual.get(0).getEmail());
        assertEquals(TEST_USERNAME, actual.get(0).getLogin());

        //verify
        verify(orgService).getMembers(anyString(), eq(null));
    }

    @Test
    void testGetAdminUsers() throws IOException {
        List<User> expected = getTestGithubUsers();
        //Mock
        when(orgService.getMembers(anyString(), eq(OrganizationService.RoleFilter.admin))).thenReturn(Collections.singletonList(TestManager.getTestGithubUser()));

        //Execute
        List<User> actual = sut.getUsers(TEST_ORGANISATION, Optional.of(OrganizationService.RoleFilter.admin));

        //Assert
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(Iterables.elementsEqual(expected, actual));

        //verify
        verify(orgService).getMembers(anyString(), eq(OrganizationService.RoleFilter.admin));
    }

    @Test
    void testGetUserInvalidRole(){
        //Assert
        IllegalArgumentException testRole = assertThrows(IllegalArgumentException.class, this::execute
        );
    }
    private void execute() throws IOException {
        sut.getUsers(TEST_ORGANISATION,
                Optional.of(OrganizationService.RoleFilter.valueOf("TestRole")));
    }

    @Test
    void testRemoveUsers() throws IOException {
        //Mock
        doNothing().when(orgService).removeMember(anyString(), anyString());

        //Execute
        sut.removeUser(TEST_ORGANISATION, TEST_USERNAME);

        //Verify
        verify(orgService).removeMember(anyString(), anyString());
    }


    @Test
    void testGetUser() throws IOException {
        //Mock
        when(userService.getUser(anyString())).thenReturn(TestManager.getTestGithubUser());

        //Execute
        User actual = sut.getUser(TEST_USERNAME);

        //Assert
        assertEquals(TEST_FULL_NAME, actual.getName());
        assertEquals(TEST_EMAIL, actual.getEmail());
        assertEquals(TEST_USERNAME, actual.getLogin());

        //verify
        verify(userService).getUser(anyString());
    }


    @Test
    void testGetTeams() throws IOException {
        List<Team> expected = getTestGithubTeams();

        //Mock
        when(teamService.getTeams(anyString())).thenReturn(Collections.singletonList(getTestGithubTeam()));

        //Execute
        List<Team> actual = sut.getTeams(TEST_ORGANISATION);

        //Assert
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(Iterables.elementsEqual(expected, actual));
    }

    @Test
    void testAddUser(){
        String testOrg = TEST_ORGANISATION;
        String testUser = TEST_USERNAME;
        String testTeam = TEST_TEAM;

    }

}