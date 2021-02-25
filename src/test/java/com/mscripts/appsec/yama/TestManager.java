package com.mscripts.appsec.yama;

import com.mscripts.appsec.yama.model.GraylogUser;
import com.mscripts.appsec.yama.model.graylog.requests.ChangePasswordRequest;
import com.mscripts.appsec.yama.model.graylog.requests.CreateUserRequest;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

public class TestManager {

    public static final String TEST_USERNAME = "testusername";
    public static final String TEST_EMAIL = "test@testemail.com";
    public static final String TEST_FULL_NAME = "Test Full Name";
    public static final String TEST_PASSWORD = "Testpassword@123";
    public static final String NEW_TEST_PASSWORD = "NewPassword@123";
    public static final String TEST_HTTP_URL = "http://test.com/";
    public static final String TEST_HTTPS_URL = "https://test.com/";
    public static final String TEST_ORGANISATION = "TestOrganisation";
    public static final String TEST_TEAM = "TestTeam";
    public static final int TEST_TEAM_ID = 1234;

    public static JSONObject testReaderGraylogUser;
    public static JSONObject testAdminGraylogUser;

    public static GraylogUser testAdminGraylogUserObj;
    public static GraylogUser testReaderGraylogUserObj;

    public static Team testGithubTeam = new Team();
    public static User testGithubUser = new User();

    public static String getAdminGraylogUserJson() throws JSONException {
        if (testAdminGraylogUser == null) {
            testAdminGraylogUser = createVanillaGraylogUser();
            setGraylogUserRole(testAdminGraylogUser, Collections.singleton(GRAYLOG_ADMIN_ROLE));
        }
        return testAdminGraylogUser.toString();
    }

    public static String getReaderGraylogUserJson() throws JSONException {
        if (testReaderGraylogUser == null) {
            testReaderGraylogUser = createVanillaGraylogUser();
        }
        return testReaderGraylogUser.toString();
    }

    public static String getTestCreateRequestJson() throws JSONException {
        return createVanillaGraylogUser().put("password", TEST_PASSWORD).toString();
    }

    public static String getChangePasswordRequestJson() throws JSONException {
        return new JSONObject().put("old_password", TEST_PASSWORD).put("password", NEW_TEST_PASSWORD).toString();
    }


    public static GraylogUser getReaderGraylogUserObj() {
        if (testReaderGraylogUserObj == null) {
            testReaderGraylogUserObj = new GraylogUser(TEST_USERNAME, TEST_EMAIL, TEST_FULL_NAME);
        }
        return testReaderGraylogUserObj;

    }

    public static GraylogUser getAdminGraylogUserObj() {
        if (testAdminGraylogUserObj == null) {
            testAdminGraylogUserObj = new GraylogUser(TEST_USERNAME, TEST_EMAIL, TEST_FULL_NAME, Collections.singleton(GRAYLOG_ADMIN_ROLE));
        }
        return testAdminGraylogUserObj;
    }


    public static CreateUserRequest getTestCreateUserRequest() {
        return CreateUserRequest.build(TEST_USERNAME, TEST_EMAIL, TEST_FULL_NAME, TEST_PASSWORD, Collections.singleton(GRAYLOG_READER_ROLE));
    }

    public static ChangePasswordRequest getTestChangePasswordRequest() {
        return ChangePasswordRequest.build(TEST_PASSWORD, NEW_TEST_PASSWORD);
    }

    private static JSONObject createVanillaGraylogUser() throws JSONException {
        JSONObject vanillaGraylogUser = new JSONObject();
        vanillaGraylogUser.put("username", TEST_USERNAME);
        vanillaGraylogUser.put("email", TEST_EMAIL);
        vanillaGraylogUser.put("full_name", TEST_FULL_NAME);
        setGraylogUserRole(vanillaGraylogUser, Collections.singleton(GRAYLOG_READER_ROLE));
        JSONArray permissions = new JSONArray();
        permissions.put("");
        vanillaGraylogUser.put("permissions", permissions);
        vanillaGraylogUser.put("startpage", JSONObject.NULL);
        vanillaGraylogUser.put("timezone", GRAYLOG_DEFAULT_TIMEZONE);
        vanillaGraylogUser.put("session_timeout_ms", GRAYLOG_DEFAULT_SESSION_TIME_OUT);
        return vanillaGraylogUser;
    }

    private static void setGraylogUserRole(JSONObject user, Set<String> roles) throws JSONException {
        JSONArray roleArray = new JSONArray();
        roles.parallelStream().forEach(roleArray::put);
        user.put("roles", roleArray);
    }

    public static User getTestGithubUser() {
        testGithubUser.setName(TEST_FULL_NAME);
        testGithubUser.setEmail(TEST_EMAIL);
        testGithubUser.setLogin(TEST_USERNAME);
        testGithubUser.setAvatarUrl(TEST_HTTPS_URL);
        return testGithubUser;
    }

    public static Team getTestGithubTeam() {
        testGithubTeam.setName(TEST_TEAM);
        testGithubTeam.setId(TEST_TEAM_ID);
        testGithubTeam.setUrl(TEST_HTTPS_URL);

        return testGithubTeam;
    }

    public static List<User> getTestGithubUsers() {
        return Collections.singletonList(getTestGithubUser());
    }

    public static List<Team> getTestGithubTeams() {
        return Collections.singletonList(getTestGithubTeam());
    }

//
//    public static Issue getTestIssue(){
//
//    }
}
