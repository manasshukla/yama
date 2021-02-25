package com.mscripts.appsec.yama.clients.aws;

import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.mscripts.appsec.yama.TestManager.TEST_USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AWSIAMClientTest {


    @Mock
    AmazonIdentityManagement client;

    @Mock
    GetAccountAuthorizationDetailsResult accountAuthorizationDetails;


    @InjectMocks
    AWSIAMClient sut;

    @Test
    void getUsers() {

        //Setup
        int ITERATIONS = 3;
        UserDetail userDetail = new UserDetail();
        userDetail.setUserName(TEST_USERNAME);
        userDetail.setPath("TestPath");

        //Mock
        when(client.getAccountAuthorizationDetails(any(GetAccountAuthorizationDetailsRequest.class))).thenReturn(accountAuthorizationDetails);
        when(accountAuthorizationDetails.isTruncated()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(accountAuthorizationDetails.getMarker()).thenReturn("testmarker");
        when(accountAuthorizationDetails.getUserDetailList()).thenReturn(Collections.singletonList(userDetail));

        //Execute
        List<UserDetail> actual = sut.getUsers();

        //Assert
        assertEquals(ITERATIONS, actual.size());
        assertEquals(userDetail, actual.get(0));
        assertEquals(userDetail, actual.get(1));
        assertEquals(userDetail, actual.get(2));

        //Verify
        verify(client, times(ITERATIONS)).getAccountAuthorizationDetails(any(GetAccountAuthorizationDetailsRequest.class));
        verify(accountAuthorizationDetails, times(ITERATIONS)).isTruncated();
        verify(accountAuthorizationDetails, times(ITERATIONS)).getMarker();
        verify(accountAuthorizationDetails, times(ITERATIONS)).getUserDetailList();
    }

//    @Test
//    @Ignore
//    void createUserWithLoginProfile() {
//    }

    @Test
    void deleteUserAndLoginProfile() {
        //Setup
        DeleteUserResult deleteUserResult = new DeleteUserResult();
        DeleteLoginProfileResult deleteLoginProfileResult = new DeleteLoginProfileResult();

        //Mock
        when(client.deleteLoginProfile(any(DeleteLoginProfileRequest.class))).thenReturn(deleteLoginProfileResult);
        when(client.deleteUser(any(DeleteUserRequest.class))).thenReturn(deleteUserResult);

        //Execute
        boolean actual = sut.deleteUserAndLoginProfile(TEST_USERNAME);

        //Assert
        assertTrue(actual);
    }
}