package com.mscripts.appsec.yama.model.graylog.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mscripts.appsec.yama.TestManager;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

class ChangePasswordRequestTest {

    @Test
    void testBuild() throws JSONException, JsonProcessingException {
        //Setup
        String expectedJSON = TestManager.getChangePasswordRequestJson();
        ChangePasswordRequest request = TestManager.getTestChangePasswordRequest();
        String actualJSON = new ObjectMapper().writeValueAsString(request);

        //Assert
        JSONAssert.assertEquals(expectedJSON, actualJSON, JSONCompareMode.STRICT);

    }
}