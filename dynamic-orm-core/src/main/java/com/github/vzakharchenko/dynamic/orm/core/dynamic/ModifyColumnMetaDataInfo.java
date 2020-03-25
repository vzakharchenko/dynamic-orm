package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;

public interface ModifyColumnMetaDataInfo extends ColumnMetaDataInfo {
    void setColumn(Path column);

    void setSize(Integer size);

    void setDecimalDigits(Integer decimalDigits);

    void setPrimaryKey(Boolean primaryKey);

    void setNullable(Boolean nullable);
}
