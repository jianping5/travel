package com.travel.travel.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.travel.travel.model.entity.Article;
import com.travel.travel.model.entity.ArticleDetail;
import com.travel.travel.model.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgy
 * @create 2023-04-10 15:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVO {

    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 用户头像
     */
    private String userAvatarUrl;
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
     * 父评论用户名
     */
    private String parentUserName;

    /**
     * 父评论用户头像
     */
    private String parentUserAvatarUrl;
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

    /**
     * 点赞量
     */
    private Integer likeCount;

    /**
     * 回复量
     */
    private Integer replyCount;

    public static CommentVO objToVo(Comment comment){
        if(comment == null){
            return null;
        }
        CommentVO commentVO = new CommentVO();
        BeanUtil.copyProperties(comment,commentVO);
        return commentVO;
    }
}
