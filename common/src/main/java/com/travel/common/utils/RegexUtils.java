package com.travel.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则工具类
 * @author jianping5
 * @createDate 15/5/2023 下午 4:17
 */
public class RegexUtils {

    /**
     * 校验 URL 是否合法
     * @param url
     * @return
     */
    public static boolean isUrlValid(String url) {
        String regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

}
