package com.travel.common.constant;

import org.apache.commons.lang3.ObjectUtils;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 7:50
 */
public enum BehaviorTypeConstant {
    /**
     * 点赞
     */
    LIKE(0, "like"),

    /**
     * 取消点赞
     */
    DISLIKE(0, "dislike"),

    /**
     * 评论
     */
    COMMENT(8, "comment"),

    /**
     * 收藏
     */
    FAVORITE(8, "favorite"),

    /**
     * 取消收藏
     */
    DISFAVORITE(8, "disfavorite"),

    /**
     * 点评
     */
    REVIEW(10, "review"),

    /**
     * 关注
     */
    FOLLOW(11, "follow"),

    /**
     * 取消关注
     */
    DISFOLLOW(11, "disfollow");

    private Integer typeIndex;

    private String typeName;

    BehaviorTypeConstant(Integer typeIndex, String typeName) {
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
