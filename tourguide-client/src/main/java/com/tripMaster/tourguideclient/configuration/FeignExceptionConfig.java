package com.tripMaster.tourguideclient.configuration;

import com.tripMaster.tourguideclient.exception.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Class that configure a bean for {@link CustomErrorDecoder} for feign
 *
 * @author Christine Duarte
 */
@Configuration
public class FeignExceptionConfig {

    @Bean
    public CustomErrorDecoder myCustomErrorDecoder() {
        return new CustomErrorDecoder();
    }
}
