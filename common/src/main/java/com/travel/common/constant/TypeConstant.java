package com.travel.common.constant;


/**
 * @author jianping5
 * @createDate 27/3/2023 下午 4:12
 */
public enum TypeConstant {

    /**
     * 用户
     */
    USER(0, "用户"),

    /**
     * 文章游记
     */
    ARTICLE(1, "文章"),

    /**
     * 视频游记
     */
    VIDEO(2, "视频"),

    /**
     * 官方
     */
    OFFICIAL(3, "官方"),

    /**
     * 团队
     */
    TEAM(4, "团队"),

    /**
     * 官方资源
     */
    OFFICIAL_RESOURCE(5, "官方资源"),

    /**
     * 资讯通知
     */
    NOTIFICATION(6, "资讯通知"),

    /**
     * 周边
     */
    DERIVATIVE(7, "周边"),

    /**
     * 评论
     */
    COMMENT(8, "评论"),

    /**
     * 动态
     */
    NEWS(9, "动态"),

    /**
     * 点评
     */
    REVIEW(10, "点评"),

    /**
     * 关注
     */
    FOLLOW(11, "关注");





    private Integer type;

    private String typeName;

    TypeConstant(Integer type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }
}
