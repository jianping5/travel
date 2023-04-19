package com.travel.data.service;

import com.travel.common.model.dto.TagAddRequest;
import com.travel.data.model.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jianping5
* @description 针对表【tag(标签表)】的数据库操作Service
* @createDate 2023-03-22 14:47:05
*/
public interface TagService extends IService<Tag> {

    /**
     * 添加自定义标签
     * @param tagAddRequest
     */
    void addCustomizedTag(TagAddRequest tagAddRequest);

}
