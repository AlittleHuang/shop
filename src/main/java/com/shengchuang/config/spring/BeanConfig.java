package com.shengchuang.config.spring;

import com.shengchuang.common.mvc.repository.CommonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;

@Configuration
public class BeanConfig {

    @Bean
    @Autowired
    public CommonDao getCommonDao(EntityManager entityManager) {
        return new CommonDao(entityManager);
    }

}
