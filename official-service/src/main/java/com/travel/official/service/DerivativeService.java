package com.travel.official.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.travel.official.model.dto.derivative.DerivativeQueryRequest;
import com.travel.official.model.entity.Derivative;
import com.baomidou.mybatisplus.extension.service.IService;
import com.travel.official.model.entity.Official;
import com.travel.official.model.vo.DerivativeVO;

import java.util.List;

/**
* @author jianping5
* @description 针对表【derivative(周边表)】的数据库操作Service
* @createDate 2023-03-30 16:50:48
*/
public interface DerivativeService extends IService<Derivative> {

    /**
     * 校验 Derivative
     * @param derivative
     * @param b
     */
    void validDerivative(Derivative derivative, boolean b);

    /**
     * 获取周边视图体
     * @param Derivative
     * @return
     */
    DerivativeVO getDerivativeVO(Derivative Derivative);

    /**
     * 获取分页周边视图体
     * @param DerivativePage
     * @return
     */
    Page<DerivativeVO> getDerivativeVOPage(Page<Derivative> DerivativePage);

    /**
     * 根据请求体获取请求 Wrapper
     * @param DerivativeQueryRequest
     * @return
     */
    QueryWrapper<Derivative> getQueryWrapper(DerivativeQueryRequest DerivativeQueryRequest);

    /**
     * 创建周边
     * @param Derivative
     * @return
     */
    Derivative addDerivative(Derivative Derivative);


    /**
     * 解散周边
     * @param Derivative
     * @return
     */
    boolean deleteDerivative(Derivative Derivative);

    /**
     * 更新周边
     * @param Derivative
     * @return
     */
    boolean updateDerivative(Derivative Derivative);


    /**
     * 获取推荐周边
     * @param current
     * @param size
     * @return
     */
    List<DerivativeVO> listRcmdDerivativeVO(long current, long size);

    /**
     * 获取周边
     * @param id
     * @return
     */
    Official obtainDerivative(Long id);

}
