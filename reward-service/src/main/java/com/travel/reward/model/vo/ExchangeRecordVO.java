package com.travel.reward.model.vo;

import com.travel.common.model.dto.user.UserDTO;
import com.travel.reward.model.entity.ExchangeRecord;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 15/4/2023 下午 7:52
 */
@Data
public class ExchangeRecordVO implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 周边 id
     */
    private Long derivativeId;

    /**
     * 代币金额
     */
    private Integer tokenAccount;

    /**
     * 兑换凭证
     */
    private String certificate;

    /**
     * 是否删除
     */
    private Integer isDeleted;

    /**
     * 关联用户
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
     * @param exchangeRecordVO
     * @return
     */
    public static ExchangeRecord voToObj(ExchangeRecordVO exchangeRecordVO) {
        if (exchangeRecordVO == null) {
            return null;
        }
        ExchangeRecord exchangeRecord = new ExchangeRecord();
        BeanUtils.copyProperties(exchangeRecordVO, exchangeRecord);
        return exchangeRecord;
    }

    /**
     * 对象转包装类
     *
     * @param exchangeRecord
     * @return
     */
    public static ExchangeRecordVO objToVo(ExchangeRecord exchangeRecord) {
        if (exchangeRecord == null) {
            return null;
        }
        ExchangeRecordVO exchangeRecordVO = new ExchangeRecordVO();
        BeanUtils.copyProperties(exchangeRecord, exchangeRecordVO);

        return exchangeRecordVO;
    }
}
