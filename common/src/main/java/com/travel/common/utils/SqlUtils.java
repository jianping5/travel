package com.travel.common.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 3:58
 */
public class SqlUtils {
    /**
     * 校验排序字段是否合法（防止 SQL 注入）
     *
     * @param sortField
     * @return
     */
    public static boolean validSortField(String sortField) {
        if (StringUtils.isBlank(sortField)) {
            return false;
        }
        return !StringUtils.containsAny(sortField, "=", "(", ")", " ");
    }
}
