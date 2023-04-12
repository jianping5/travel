package com.travel.data.service;

import com.travel.common.model.vo.SearchVDTO;
import com.travel.data.model.dto.PersonalRcmdRequest;
import com.travel.data.model.entity.Behavior;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author jianping5
* @description 针对表【behavior(用户行为表)】的数据库操作Service
* @createDate 2023-03-22 14:47:05
*/
public interface BehaviorService extends IService<Behavior> {

    /**
     * 获取指定类型的个性化推荐
     * @param personalRcmdRequest
     * @return
     */
    SearchVDTO listPersonalRcmd(PersonalRcmdRequest personalRcmdRequest);
}
