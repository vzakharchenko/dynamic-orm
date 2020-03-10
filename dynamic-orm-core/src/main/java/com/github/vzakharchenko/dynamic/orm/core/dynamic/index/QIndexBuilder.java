package com.github.vzakharchenko.dynamic.orm.core.dynamic.index;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.querydsl.core.types.Path;

public interface QIndexBuilder {
    QTableBuilder buildIndex(Path<?> columnName, boolean unique);

    QTableBuilder buildIndex(String columnName, boolean unique);
}
