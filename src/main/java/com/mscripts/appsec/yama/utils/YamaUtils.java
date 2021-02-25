package com.mscripts.appsec.yama.utils;

import com.mscripts.appsec.yama.constants.YSystem;
import com.mscripts.appsec.yama.constants.YamaConstants;
import com.mscripts.appsec.yama.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mscripts.appsec.yama.constants.YamaConstants.*;

@Slf4j
public class YamaUtils {

    public static final String JQL_FORMED = "JQL formed : {}";

    private YamaUtils() {
    }

    /**
     * @param length Intended length of the complex password
     * @return complex password comprising of alphanumeric string and special characters
     */
    public static String createComplexPassword(int length) {
        //throw ValidationException in case password length is less than minimum  acceptable length
        validatePasswordLength(length);

        String randomAlphaNumeric = RandomStringUtils.randomAlphanumeric(length - 4);
        String randomNumerals = RandomStringUtils.randomNumeric(2);
        String randomSpecialChars = RandomStringUtils.random(2, 35, 41, false, false,null, new SecureRandom());
        String randomPassword = randomAlphaNumeric + randomSpecialChars+ randomNumerals;
        List<Character> randomChars = randomPassword.chars().parallel().mapToObj(c -> (char) c).collect(Collectors.toList());
        //Shuffle the characters around to get more randomisation
        Collections.shuffle(randomChars);
        //append all characters to a String. Parallel stream adds to the randomisation
        return randomChars.parallelStream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

    }

    private static void validatePasswordLength(int length) {
        if (length < YamaConstants.MIN_PASSWORD_LENGTH_PER_POLICY) {
            throw new ValidationException("Password length violates minimum password length policy");
        }
    }

    public static String getJQLQuery(YSystem system, String state) {
        switch(state){
            case PROVISION:
                log.info(JQL_FORMED, MessageFormat.format(PROVISIONING_JQL, system));
                return MessageFormat.format(PROVISIONING_JQL, system);
            case DEPROVISION:
                log.info(JQL_FORMED, MessageFormat.format(DEPROVISIONING_JQL, system));
                return MessageFormat.format(DEPROVISIONING_JQL, system);
            case TIMEBOUND_DEPROVISION:
                log.info(JQL_FORMED, MessageFormat.format(TIMEBOUND_DEPROVISIONING_JQL, system));
                return MessageFormat.format(TIMEBOUND_DEPROVISIONING_JQL, system);
            default:
                throw new ValidationException("Not a valid state. State could either be PROVISION or DEPROVISION");
        }
    }
}
