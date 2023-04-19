package com.travel.user.registry;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.travel.common.constant.TypeConstant;
import com.travel.common.model.entity.Article;
import com.travel.common.model.entity.Comment;
import com.travel.common.model.entity.Video;
import com.travel.user.model.entity.UserInfo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author zgy
 * @create 2023-04-17 18:27
 */
@Component
public class UpdateWrapperRegistry {


    @PostConstruct
    public void doInit() {

    }

    public UpdateWrapper getUpdateWrapperByType(Integer typeId) {
        if (TypeConstant.ARTICLE.getTypeIndex().equals(typeId)) {
            return new UpdateWrapper<Article>();
        }
        if(TypeConstant.VIDEO.getTypeIndex().equals(typeId)){
            return new UpdateWrapper<Video>();
        }
        if(TypeConstant.COMMENT.getTypeIndex().equals(typeId)){
            return new UpdateWrapper<Comment>();
        }
        if(TypeConstant.USER.getTypeIndex().equals(typeId)){
            return new UpdateWrapper<UserInfo>();
        }
        return null;
    }
}
