package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.querydsl.core.types.Path;
import org.apache.commons.lang3.StringUtils;
import com.github.vzakharchenko.dynamic.orm.core.helper.FormatHelper;

import java.io.Serializable;

/**
 *
 */
public abstract class SoftDeleteFactory {

    public static <TYPE extends Serializable> SoftDelete<TYPE> createSoftDelete(
            Path<TYPE> column,
            TYPE value,
            TYPE defaultValue) {
        return new SoftDelete<>(column, value, defaultValue);
    }

    public static SoftDelete<Serializable> createSoftDeleteString(Path<?> column,
                                                                  String value,
                                                                  String defaultValue) {
        Class<?> type = column.getType();
        Serializable defaultValueType =
                StringUtils.isNotEmpty(defaultValue)
                        ? (Serializable) FormatHelper.transformObjectValue(defaultValue, type)
                        : null;
        return StringUtils.isNotEmpty(value)
                ? createSoftDelete((Path) column,
                (Serializable) FormatHelper
                        .transformObjectValue(value, type), defaultValueType)
                : createSoftDelete((Path) column, null, defaultValueType);
    }
}
