package com.travel.user.constant;

/**
 * @author zgy
 * @create 2023-03-31 16:02
 */
public enum CredentialType {
    ACCOUNT,
    EMAIL,
    PHONE_NUMBER;

    public static boolean isValid(Integer type) {
        if (type == null) {
            return false;
        }
        for (CredentialType credentialType : CredentialType.values()) {
            if (credentialType.ordinal() == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAccount(Integer type) {
        if (type == null) {
            return false;
        }
        return ACCOUNT.ordinal() == type;
    }

    public static boolean isEmail(Integer type) {
        if (type == null) {
            return false;
        }
        return EMAIL.ordinal() == type;
    }

    public static boolean isPhoneNumber(Integer type) {
        if (type == null) {
            return false;
        }
        return PHONE_NUMBER.ordinal() == type;
    }
}
