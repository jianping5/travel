package com.travel.team.config;

import com.travel.common.config.BaseSwaggerConfig;
import com.travel.common.domain.SwaggerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author jianping5
 * @createDate 18/3/2023 下午 8:37
 */

@Configuration
@EnableSwagger2
@Profile(value = {"dev"})
public class SwaggerConfiguration extends BaseSwaggerConfig {

    @Override
    public SwaggerProperties swaggerProperties() {
        return SwaggerProperties.builder()
                .apiBasePackage("com.travel.team.controller")
                .title("团队服务")
                .description("团队服务接口文档")
                .version("0.1.0")
                .build();
    }
}

