package com.travel.common.constant;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author jianping5
 * @createDate 27/3/2023 下午 4:12
 */
public enum TypeConstant {

    /**
     * 用户
     */
    USER(0, "user"),

    /**
     * 文章游记
     */
    ARTICLE(1, "article"),

    /**
     * 视频游记
     */
    VIDEO(2, "video"),

    /**
     * 官方
     */
    OFFICIAL(3, "official"),

    /**
     * 团队
     */
    TEAM(4, "team"),

    /**
     * 官方资源
     */
    OFFICIAL_RESOURCE(5, "officialResource"),

    /**
     * 资讯通知
     */
    NOTIFICATION(6, "notification"),

    /**
     * 周边
     */
    DERIVATIVE(7, "derivative"),

    /**
     * 评论
     */
    COMMENT(8, "comment"),

    /**
     * 动态
     */
    NEWS(9, "news"),

    /**
     * 点评
     */
    REVIEW(10, "review"),

    /**
     * 关注
     */
    FOLLOW(11, "follow");

    private Integer typeIndex;

    private String typeName;

    TypeConstant(Integer typeIndex, String typeName) {
        this.typeIndex = typeIndex;
        this.typeName = typeName;
    }

    public Integer getTypeIndex() {
        return typeIndex;
    }

    public String getTypeName() {
        return typeName;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static TypeConstant getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (TypeConstant anEnum : TypeConstant.values()) {
            if (anEnum.getTypeName().equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    /**
     * 判断枚举类是否包含指定的 key
     * @param typeIndex
  翻新eturn
     */
    public static boolean containsKey(Integer typeIndex) {
        for (TypeConstant typeConstant : TypeConstant.values()) {
            if (typeConstant.getTypeIndex().equals(typeIndex)) {
                return true;
            }
        }
        return false;
    }
}
