package com.github.vzakharchenko.dynamic.orm.core.dynamic.column;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicBuilderContext;

public interface QTableColumnContext extends QTableColumn {
    QDynamicBuilderContext getContext();
}
