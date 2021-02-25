package com.mscripts.appsec.yama.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscripts.appsec.yama.TestManager;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Collections;

import static com.mscripts.appsec.yama.TestManager.*;
import static com.mscripts.appsec.yama.constants.YamaConstants.GRAYLOG_ADMIN_ROLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class GraylogUserTest {


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void testSerialisation() throws JsonProcessingException, JSONException {
        //Setup
        String expectedJson = TestManager.getAdminGraylogUserJson();
        GraylogUser user = new GraylogUser(TEST_USERNAME,TEST_EMAIL,TEST_FULL_NAME, Collections.singleton(GRAYLOG_ADMIN_ROLE));
        String actualJson = new ObjectMapper().writeValueAsString(user);
        System.out.println(actualJson);
        System.out.println(expectedJson);
        assertEquals(expectedJson, actualJson, JSONCompareMode.STRICT);

    }


    @Test
    void eqalsAndToString() {
        GraylogUser user1 = new GraylogUser(TEST_USERNAME,TEST_EMAIL,TEST_FULL_NAME);
        GraylogUser user2 = user1;
        GraylogUser user3 = new GraylogUser("test3","test@test.com","Full Name3");
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    void testHashCode(){
        GraylogUser user = new GraylogUser(TEST_USERNAME,TEST_EMAIL,TEST_FULL_NAME);
        assertEquals(TEST_USERNAME.hashCode(), user.hashCode());
        assertNotNull(user.toString());
    }

}