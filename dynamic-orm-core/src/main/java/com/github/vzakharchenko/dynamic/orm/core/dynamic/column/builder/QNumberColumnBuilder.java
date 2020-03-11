package com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder;

public interface QNumberColumnBuilder
        <RETURN_TYPE, BUILDER_TYPE extends QNumberColumnBuilder<RETURN_TYPE, ?>>
        extends QSizeColumnBuilder<RETURN_TYPE, BUILDER_TYPE> {

    BUILDER_TYPE decimalDigits(Integer decimalDigits);
}
