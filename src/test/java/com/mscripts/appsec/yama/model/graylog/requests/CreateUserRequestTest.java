package com.mscripts.appsec.yama.model.graylog.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscripts.appsec.yama.TestManager;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

class CreateUserRequestTest {

    @Test
    void testBuild() throws JSONException, JsonProcessingException {
        //Setup
        String expectedJSON = TestManager.getTestCreateRequestJson();
        CreateUserRequest request = TestManager.getTestCreateUserRequest();
        String actualJSON = new ObjectMapper().writeValueAsString(request);

        //Assert
        assertEquals(expectedJSON, actualJSON, JSONCompareMode.STRICT);
    }
}