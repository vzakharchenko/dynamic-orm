package com.github.vzakharchenko.dynamic.orm.core;

import org.springframework.context.annotation.Bean;

public interface ISpringOrmQueryFactory {

    @Bean
    OrmQueryFactory getInstance();
}
