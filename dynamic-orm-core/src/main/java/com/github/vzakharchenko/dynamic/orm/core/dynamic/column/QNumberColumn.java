package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

public interface QNumberColumn extends QSizeColumn {
    Class<? extends Number> numberClass();

    Integer decimalDigits();
}
