package com.ecommerce.config;

import com.ecommerce.adapter.in.web.auth.common.resolver.AccessTokenResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AccessTokenResolver accessTokenResolver;

    public WebConfig(AccessTokenResolver accessTokenResolver) {
        this.accessTokenResolver = accessTokenResolver;
        log.info(">>> WebConfig initialized and registered AccessTokenResolver <<<");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(accessTokenResolver);
        log.info(">>> Added AccessTokenResolver to Spring MVC Argument Resolvers <<<");
    }
}

