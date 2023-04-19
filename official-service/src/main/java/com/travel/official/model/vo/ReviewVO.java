package com.travel.official.model.vo;

import com.travel.common.model.dto.user.UserDTO;
import com.travel.official.model.entity.Review;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 6/4/2023 下午 8:26
 */
@Data
public class ReviewVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 点评类型
     */
    private Integer reviewObjType;

    /**
     * 点评对象
     */
    private Long reviewObjId;

    /**
     * 点评内容
     */
    private String content;

    /**
     * 地理位置
     */
    private String location;

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 回复量
     */
    private Integer replyCount;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     * 是否点赞
     */
    private Integer isLiked;

    /**
     * 发布用户
     */
    private UserDTO user;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param reviewVO
     * @return
     */
    public static Review voToObj(ReviewVO reviewVO) {
        if (reviewVO == null) {
            return null;
        }
        Review review = new Review();
        BeanUtils.copyProperties(reviewVO, review);
        return review;
    }

    /**
     * 对象转包装类
     *
     * @param review
     * @return
     */
    public static ReviewVO objToVo(Review review) {
        if (review == null) {
            return null;
        }
        ReviewVO reviewVO = new ReviewVO();
        BeanUtils.copyProperties(review, reviewVO);

        return reviewVO;
    }
}
