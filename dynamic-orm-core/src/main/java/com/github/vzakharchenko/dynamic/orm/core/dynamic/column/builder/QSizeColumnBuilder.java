package com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder;

public interface QSizeColumnBuilder<RETURN_TYPE,
        BUILDER_TYPE extends QSizeColumnBuilder<RETURN_TYPE, ?>>
        extends QColumnBuilder<RETURN_TYPE, BUILDER_TYPE> {
    BUILDER_TYPE size(Integer size);

}
