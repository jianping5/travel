package com.travel.travel.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jianping5
 * @createDate 23/3/2023 下午 2:01
 */
@Data
public class CommentAddRequest implements Serializable {

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 评论类型（文章、视频）
     */
    private Integer commentObjType;

    /**
     * 评论类型下的对象 id
     */
    private Long commentObjId;

    /**
     * 父评论用户 id
     */
    private Long parentUserId;

    /**
     * 顶级评论 id
     */
    private Long topId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 地理位置
     */
    private String location;

    private static final long serialVersionUID = 1L;

}
