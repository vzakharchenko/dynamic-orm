package com.github.vzakharchenko.dynamic.orm.core.dynamic.column.builder;

public interface QColumnBuilder<RETURN_TYPE,
        BUILDER_TYPE extends QColumnBuilder<RETURN_TYPE, ?>> {
    BUILDER_TYPE notNull();

    BUILDER_TYPE nullable();

    BUILDER_TYPE useAsPrimaryKey();

    BUILDER_TYPE usAsNotPrimaryKey();

    RETURN_TYPE create();
}
