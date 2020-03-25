package com.github.vzakharchenko.dynamic.orm.core.dynamic.index;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;

/**
 * SQL Index builder
 */
public interface QIndexBuilder {
    /**
     * clustered Index
     *
     * @return Index Builder
     */
    QIndexBuilder clustered();

    /**
     * add Index
     *
     * @return Table Builder
     */
    QTableBuilder addIndex();

    /**
     * add unique index
     *
     * @return Table Builder
     */
    QTableBuilder addUniqueIndex();

    /**
     * Drop Index
     * @return Table Builder
     */
    QTableBuilder drop();
}
