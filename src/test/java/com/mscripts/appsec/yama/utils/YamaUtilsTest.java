package com.mscripts.appsec.yama.utils;

import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.exception.ValidationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;
import static org.junit.jupiter.api.Assertions.*;

class YamaUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Throw ValidationException if the password is less than the minimum acceptable length")
    void createComplexPasswordThrowsException() {
        assertThrows(ValidationException.class,
                () -> YamaUtils.createComplexPassword(4));
    }

    @Test
    @DisplayName("Generated password is of specified length")
    void createComplexPasswordCheckLength() {
        int length = 10;
        String password = YamaUtils.createComplexPassword(length);
        assertEquals(password.length(), length, "Password should be of specified length");

    }

    @Test
    @DisplayName("Generated password contains 2 special characters")
    void createComplexPasswordContainsSpecialChars() {
        String password = YamaUtils.createComplexPassword(10);
        int specialCharCount = 0;
        for (char c : password.toCharArray()) {
            if (c >= 35 && c <= 41) {
                specialCharCount++;
            }
        }
        assertEquals(2, specialCharCount, "Password should contains 2 special characters");
    }

    @Test
    @DisplayName("Generated password contains numerals")
    void createComplexPasswordContainsNumeric() {
            String password = YamaUtils.createComplexPassword(10);
        int numerals = 0;
        for (char c : password.toCharArray()) {
            if (c >= 48 && c <= 57) {
                numerals++;
            }
        }
        assertTrue(numerals > 0, "Password should contain numerals");
    }


    @Test
    @DisplayName("Test Provision JQL Query")
    void testProvisionJQL() {
        String expected = "project = AR AND SYSTEM = GITHUB AND status = APPROVED AND \"ISSUE TYPE\"=\"Add Access\"";
        String actual = YamaUtils.getJQLQuery(YSystem.GITHUB, PROVISION);
        assertEquals(expected, actual);
    }


    @Test
    @DisplayName("Test Deprovision JQL Query")
    void testDeprovisionJQL() {
        String expected = "project=AR AND SYSTEM = GITHUB AND \"Issue Type[Dropdown]\" = \"Remove Access\" AND  status in (APPROVED, PROVISIONED)";
        String actual = YamaUtils.getJQLQuery(YSystem.GITHUB, DEPROVISION);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test timebound deprovision JQL Query")
    void testTimeBoundDeprovisionJQL() {
        String expected = "project=AR AND SYSTEM = GITHUB AND status=PROVISIONED AND \"Access Time Limit[Radio Buttons]\" = Temporary AND \"access_end_date[Date]\" = endOfDay()";
        String actual = YamaUtils.getJQLQuery(YSystem.GITHUB, TIMEBOUND_DEPROVISION);
        assertEquals(expected, actual);
    }


    enum GlPharmacies{
        Costco("co"),
        Fruth("fr"),
        Wegmans("wg");
        private final String pharmPrefix;

        GlPharmacies(String pharmPrefix) {
            this.pharmPrefix = pharmPrefix;
        }
    }


}