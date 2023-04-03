package com.travel.user.constant;

/**
 * @author zgy
 * @create 2023-04-01 22:35
 */
public enum CodeType {
    REGISTER,
    LOGIN,
    CHANGE_PASSWORD;

    public static boolean isCodeType(int value) {
        for (CodeType e : CodeType.values()) {
            if (e.ordinal() == value) {
                return true;
            }
        }
        return false;
    }

    public static boolean equals(Integer intValue, CodeType myEnum) {
        return myEnum != null && intValue != null && myEnum.ordinal() == intValue;
    }
}