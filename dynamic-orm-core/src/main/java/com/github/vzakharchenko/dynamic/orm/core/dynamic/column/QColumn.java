package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

public interface QColumn {
    String columnName();

    Boolean notNull();

    Boolean isPrimaryKey();

}
