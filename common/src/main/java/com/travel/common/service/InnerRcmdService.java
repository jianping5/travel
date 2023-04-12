package com.travel.common.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Set;

/**
 * @author jianping5
 * @createDate 11/4/2023 下午 8:39
 */
public interface InnerRcmdService<T> {

    /**
     * 根据 id 列表获取对应类型的实体数据
     * @param idList
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> listPersonalRcmd(Set<Long> idList, long pageNum, long pageSize);
}
