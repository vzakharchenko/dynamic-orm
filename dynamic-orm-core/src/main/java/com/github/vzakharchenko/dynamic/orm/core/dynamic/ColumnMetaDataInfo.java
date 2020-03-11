package com.github.vzakharchenko.dynamic.orm.core.dynamic;


import com.querydsl.core.types.Path;

import java.io.Serializable;

/**
 *
 */
public interface ColumnMetaDataInfo extends Serializable {

    Path getColumn();

    Integer getSize();

    String getJdbcType();

    Integer getDecimalDigits();

    Boolean isNullable();

    Boolean isPrimaryKey();
}
