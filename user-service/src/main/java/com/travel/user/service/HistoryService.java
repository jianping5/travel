package com.travel.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.user.model.entity.History;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.user.model.request.HistoryQueryRequest;

/**
* @author jianping5
* @description 针对表【history(历史记录表)】的数据库操作Service
* @createDate 2023-03-22 14:34:09
*/
public interface HistoryService extends IService<History> {



    /**
     * 校验 History
     * @param history
     * @param b
     */
    void validHistory(History history, boolean b);


    /**
     * 获取历史记录列表
     */
    Page<History> queryHistory(HistoryQueryRequest historyQueryRequest);

    /**
     * 历史记录
     * @param History
     * @return
     */
    History addHistory(History History);


    /**
     * 删除历史记录
     * @param History
     * @return
     */
    boolean deleteHistory(History History);
}
