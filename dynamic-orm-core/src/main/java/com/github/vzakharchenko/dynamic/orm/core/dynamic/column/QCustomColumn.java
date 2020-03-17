package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

import com.querydsl.core.types.Path;

public interface QCustomColumn extends QSizeColumn {
    Path<?> customColumn();

    String jdbcType();

    Integer decimalDigits();
}
