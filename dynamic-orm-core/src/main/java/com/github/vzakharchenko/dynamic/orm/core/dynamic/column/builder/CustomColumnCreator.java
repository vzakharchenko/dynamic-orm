package com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;

public interface CustomColumnCreator {
    Path<?> create(PathMetadata metadata);
}
