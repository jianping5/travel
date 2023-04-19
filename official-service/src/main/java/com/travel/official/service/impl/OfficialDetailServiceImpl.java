package com.travel.official.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travel.common.common.ErrorCode;
import com.travel.common.exception.ThrowUtils;
import com.travel.official.mapper.OfficialDetailMapper;
import com.travel.official.model.entity.OfficialDetail;
import com.travel.official.service.OfficialDetailService;
import org.springframework.stereotype.Service;

/**
* @author jianping5
* @description 针对表【official_detail(官方详情表)】的数据库操作Service实现
* @createDate 2023-03-22 14:45:55
*/
@Service
public class OfficialDetailServiceImpl extends ServiceImpl<OfficialDetailMapper, OfficialDetail>
    implements OfficialDetailService{

    @Override
    public void validOfficialDetail(OfficialDetail officialDetail) {
        // 分别校验官方详情和官方 id
        String detail = officialDetail.getDetail();
        Long officialId = officialDetail.getOfficialId();

        ThrowUtils.throwIf(detail == null, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(officialId == null || officialId < 0, ErrorCode.PARAMS_ERROR);
    }

    @Override
    public boolean addOfficialDetail(OfficialDetail officialDetail) {

        // 添加到数据库中
        boolean saveResult = this.save(officialDetail);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);

        // todo：将前几句话添加到官方的 intro 字段中（是否可以由前端发过来）

        // 返回该官方详情的 id
        return true;
    }

    @Override
    public boolean updateOfficialDetail(OfficialDetail officialDetail) {

        // todo：判断是否存在该官方详情（是否需要）

        // todo：有前端ntro 字段中

        // 更新数据库
        boolean updateResult = this.updateById(officialDetail);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
        return true;
    }

}




