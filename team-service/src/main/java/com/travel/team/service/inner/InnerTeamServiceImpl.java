package com.travel.team.service.inner;

import com.travel.common.service.InnerTeamService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author jianping5
 * @createDate 22/3/2023 下午 8:27
 */
@DubboService
public class InnerTeamServiceImpl implements InnerTeamService {
    @Override
    public String sayHello() {
        return "Hello";
    }
}
