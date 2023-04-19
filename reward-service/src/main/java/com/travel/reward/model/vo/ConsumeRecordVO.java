package com.travel.reward.model.vo;

import com.travel.common.model.dto.user.UserDTO;
import com.travel.reward.model.entity.ConsumeRecord;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author jianping5
 * @createDate 15/4/2023 下午 7:52
 */
@Data
public class ConsumeRecordVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 消费信息
     */
    private String content;

    /**
     * 代币金额
     */
    private Integer tokenAccount;

    /**
     * 消费关联类型
     */
    private Integer consumeType;

    /**
     * 消费关联对象 id
     */
    private Long consumeId;

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
     * @param consumeRecordVO
     * @return
     */
    public static ConsumeRecord voToObj(ConsumeRecordVO consumeRecordVO) {
        if (consumeRecordVO == null) {
            return null;
        }
        ConsumeRecord consumeRecord = new ConsumeRecord();
        BeanUtils.copyProperties(consumeRecordVO, consumeRecord);
        return consumeRecord;
    }

    /**
     * 对象转包装类
     *
     * @param consumeRecord
     * @return
     */
    public static ConsumeRecordVO objToVo(ConsumeRecord consumeRecord) {
        if (consumeRecord == null) {
            return null;
        }
        ConsumeRecordVO consumeRecordVO = new ConsumeRecordVO();
        BeanUtils.copyProperties(consumeRecord, consumeRecordVO);

        return consumeRecordVO;
    }
}
