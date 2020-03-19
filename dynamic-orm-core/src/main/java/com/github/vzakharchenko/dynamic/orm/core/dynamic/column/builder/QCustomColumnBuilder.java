package com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder;

import liquibase.datatype.LiquibaseDataType;

public interface QCustomColumnBuilder
        <RETURN_TYPE, BUILDER_TYPE extends QCustomColumnBuilder<RETURN_TYPE, ?>>
        extends QNumberColumnBuilder<RETURN_TYPE, BUILDER_TYPE> {

    BUILDER_TYPE jdbcType(LiquibaseDataType dataType);

    BUILDER_TYPE jdbcType(String liquibaseType);

    BUILDER_TYPE column(CustomColumnCreator customColumnCreator);
}
