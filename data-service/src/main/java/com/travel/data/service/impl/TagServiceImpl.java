package com.travel.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.ThrowUtils;
import com.travel.common.model.dto.TagAddRequest;
import com.travel.data.mapper.TagMapper;
import com.travel.data.model.entity.Tag;
import com.travel.data.service.TagService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author jianping5
* @description 针对表【tag(标签表)】的数据库操作Service实现
* @createDate 2023-03-22 14:47:05
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

    @Resource
    private Gson gson;

    @Override
    public void addCustomizedTag(TagAddRequest tagAddRequest) {
        String tagList = tagAddRequest.getTagList();
        Integer tagType = tagAddRequest.getTagType();
        List<String> tagStrList = gson.fromJson(tagList, new TypeToken<List<String>>() {}.getType());
        // todo：暂时没考虑标签重复的问题
        List<Tag> tagObjList = tagStrList.stream().map(tagStr -> {
            Tag tag = new Tag();
            tag.setTagName(tagStr);
            tag.setTagType(tagType);
            return tag;
        }).collect(Collectors.toList());

        boolean result = this.saveBatch(tagObjList);
        // todo：这里的错误怎么处理？
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
    }

}




