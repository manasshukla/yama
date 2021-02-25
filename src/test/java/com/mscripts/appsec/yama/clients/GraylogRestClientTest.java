//package com.mscripts.appsec.yama.clients;
//
//import com.mscripts.appsec.yama.TestManager;
//import com.mscripts.appsec.yama.clients.graylog.GraylogRestClient;
//import com.mscripts.appsec.yama.model.GraylogUser;
//import com.mscripts.appsec.yama.model.graylog.requests.ChangePasswordRequest;
//import com.mscripts.appsec.yama.model.graylog.requests.CreateUserRequest;
//import org.graylog2.rest.models.users.responses.UserList;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static com.mscripts.appsec.yama.TestManager.*;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Disabled
//class GraylogRestClientTest {
//
//    @Autowired
//    private GraylogRestClient graylogRestClient;
//
//    private final GraylogUser testUserObj = TestManager.getReaderGraylogUserObj();
//
//
//    private final String url = "http://uatgraylog.remscripts.com:9000/api/users";
//    private final String id = "graylog";
//    private final String key = "graylog";
//
//
//    private final String json = "UserList{users=[UserSummary{id=local:admin, username=admin, email=, fullName=Administrator, permissions=[*\n" +
//            "            ], preferences={updateUnfocussed=false, enableSmartSearch=true\n" +
//            "            }, timezone=UTC, sessionTimeoutMs=28800000, readOnly=true, external=false, startpage=null, roles=[Admin\n" +
//            "            ], sessionActive=false, lastActivity=null, clientAddress=null\n" +
//            "        }\n" +
//            "    ]\n" +
//            "}";
//
//
//    @Test
//    @Order(1)
//    void testCreateUser(){
//        //Execute
//        CreateUserRequest createUserRequest = TestManager.getTestCreateUserRequest();
//        boolean userCreated = graylogRestClient.createUser(url, id, key, createUserRequest);
//        //assert
//        assertTrue(userCreated);
//    }
//
//
//    @Test
//    @Order(2)
//    void testGetUserList() {
//        //Execute
//        UserList userList = graylogRestClient.getUserList(url, id, key);
//        System.out.println(userList);
//        //assert
//        assertNotNull(userList);
//        assertTrue(userList.users().parallelStream().anyMatch(userSummary -> userSummary.username().equals(testUserObj.username)));
//    }
//
//    @Test
//    @Order(3)
//    void testchangeUserPassword(){
//        ChangePasswordRequest changePasswordRequest = TestManager.getTestChangePasswordRequest();
//        boolean passwordChanged = graylogRestClient.changeUserPassword(url, id, key, TEST_USERNAME, TEST_PASSWORD, NEW_TEST_PASSWORD);
//        assertTrue(passwordChanged);
//    }
//
//
//
//    @Test
//    @Order(4)
//    void testDeleteUser(){
//        //Execute
//        boolean userDeleted = graylogRestClient.deleteUser(url, id, key, TEST_USERNAME);
//        //assert
//        assertTrue(userDeleted);
//    }
//
//
//}