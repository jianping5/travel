package com.travel.data.datasource;

import com.travel.common.constant.TypeConstant;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源注册器
 * @author jianping5
 */
@Component
public class DataSourceRegistry {

    @Resource
    private TeamDataSource teamDataSource;

    @Resource
    private OfficialDataSource officialDataSource;

    @Resource
    private DerivativeDataSource derivativeDataSource;

    private Map<String, DataSource<T>> typeDataSourceMap;

    @PostConstruct
    public void doInit() {
        System.out.println(1);
        typeDataSourceMap = new HashMap(20) {{
            put(TypeConstant.TEAM.getTypeName(), teamDataSource);
            put(TypeConstant.OFFICIAL.getTypeName(), officialDataSource);
            put(TypeConstant.DERIVATIVE.getTypeName(), derivativeDataSource);
        }};
    }

    public DataSource getDataSourceByType(String type) {
        if (typeDataSourceMap == null) {
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}
