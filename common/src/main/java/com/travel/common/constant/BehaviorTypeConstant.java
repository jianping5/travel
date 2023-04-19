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
    DISLIKE(1, "dislike"),

    /**
     * 评论
     */
    COMMENT(2, "comment"),

    /**
     * 收藏
     */
    FAVORITE(3, "favorite"),

    /**
     * 取消收藏
     */
    DISFAVORITE(4, "disfavorite"),

    /**
     * 点评
     */
    REVIEW(5, "review"),

    /**
     * 关注
     */
    FOLLOW(6, "follow"),

    /**
     * 取消关注
     */
    DISFOLLOW(7, "disfollow"),

    /**
     * 浏览
     */
    VIEW(8, "view");

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
    public static BehaviorTypeConstant getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (BehaviorTypeConstant anEnum : BehaviorTypeConstant.values()) {
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
        for (BehaviorTypeConstant behaviorTypeConstant : BehaviorTypeConstant.values()) {
            if (behaviorTypeConstant.getTypeIndex().equals(typeIndex)) {
                return true;
            }
        }
        return false;
    }
}
