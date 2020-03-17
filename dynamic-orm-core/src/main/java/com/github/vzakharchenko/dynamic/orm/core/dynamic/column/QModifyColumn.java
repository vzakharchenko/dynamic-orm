package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

public interface QModifyColumn {

    QModifyColumn removeColumn(String... column);

    QModifyColumn size(String column, int newSize);

    QModifyColumn decimalDigits(String column, int newDecimalDigits);

    QModifyColumn nullable(String column);

    QModifyColumn notNull(String column);

    <T extends Number & Comparable<?>> QModifyColumn changeNumberType(String column,
                                                                      Class<T> tclass);

    QTableColumn finish();
}
