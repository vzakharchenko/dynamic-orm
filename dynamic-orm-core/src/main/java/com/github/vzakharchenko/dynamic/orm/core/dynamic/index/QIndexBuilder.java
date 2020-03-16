package com.github.vzakharchenko.dynamic.orm.core.dynamic.index;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;

public interface QIndexBuilder {

    QIndexBuilder clustered();

    QTableBuilder buildIndex();

    QTableBuilder buildUniqueIndex();
}
