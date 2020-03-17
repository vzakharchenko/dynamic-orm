package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;

public interface ModifyColumnMetaDataInfo {
    void setColumn(Path column);

    void setSize(Integer size);

    void setDecimalDigits(Integer decimalDigits);

    void setNullable(Boolean nullable);
}
