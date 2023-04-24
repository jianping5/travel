package com.travel.common.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 */
public enum FileUploadBizEnum {

    /**
     * 用户头像
     */
    USER_AVATAR("用户头像", "user_avatar"),

    /**
     * 游记封面
     */
    TRAVEL_COVER("游记封面", "travel_cover"),

    /**
     * 游记视频
     */
    TRAVEL_VIDEO("游记视频", "travel_video"),

    /**
     * 官方封面
     */
    OFFICIAL_COVER("官方封面", "official_cover"),

    /**
     * 官方视频
     */
    OFFICIAL_VIDEO("官方视频", "official_video"),

    /**
     * 官方材料
     */
    OFFICIAL_MATERIAL("官方材料", "official_material"),

    /**
     * 团队封面
     */
    TEAM_COVER("团队封面", "team_cover"),

    /**
     * 周边封面
     */
    DERIVATIVE_COVER("周边封面", "derivative_cover"),

    /**
     * 资讯通知封面
     */
    NOTIFICATION_COVER("资讯通知封面", "notification_cover");

    private final String text;

    private final String value;

    FileUploadBizEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileUploadBizEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
