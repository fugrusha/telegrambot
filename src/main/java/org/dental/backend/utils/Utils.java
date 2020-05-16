package org.dental.backend.utils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.regex.Pattern;

public class Utils {

    private static final String PHONE_NUMBER_GARBAGE_REGEX = "[()\\s-]+";
    private static final String PHONE_NUMBER_REGEX = "((\\+38)?\\(?\\d{3}\\)?[\\s.-]?(\\d{7}|\\d{3}[\\s.-]\\d{2}[\\s.-]\\d{2}|\\d{3}-\\d{4}))";
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile(PHONE_NUMBER_REGEX);

    public static boolean isValidEmailAddress(String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException ex) {
            return false;
        }

        return true;

//        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null
                && PHONE_NUMBER_PATTERN
                .matcher(phoneNumber.replaceAll(PHONE_NUMBER_GARBAGE_REGEX, "")).matches();
    }
}
