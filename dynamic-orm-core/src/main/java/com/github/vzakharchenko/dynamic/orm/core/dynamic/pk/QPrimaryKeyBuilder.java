package com.github.vzakharchenko.dynamic.orm.core.dynamic.pk;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.QTableBuilder;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.querydsl.core.types.Path;

/**
 * Primary Key Builder.
 */
public interface QPrimaryKeyBuilder {

    /**
     * add column as primary key for current table
     *
     * @param column - column
     * @return builder
     */
    QPrimaryKeyBuilder addPrimaryKey(Path column);

    /**
     * remove column from composite Primary Key
     *
     * @param column - column
     * @return builder
     */
    QPrimaryKeyBuilder removePrimaryKey(Path column);

    /**
     * add column as primary key for current table
     *
     * @param columnName - column name
     * @return builder
     */
    QPrimaryKeyBuilder addPrimaryKey(String columnName);

    /**
     * remove column from composite Primary Key
     *
     * @param columnName - column name
     * @return builder
     */
    QPrimaryKeyBuilder removePrimaryKey(String columnName);


    /**
     * add Primary Key Generator (works only with non composite Primary Key)
     *
     * @param pkGenerator new PKGeneratorSequence(String sequenceName) -  SQL sequence generator
     *                    PrimaryKeyGenerators.INTEGER.getPkGenerator() - unique integer generator
     *                    PrimaryKeyGenerators.LONG.getPkGenerator() - unique long generator
     *                    PrimaryKeyGenerators.UUID.getPkGenerator() - UUID String generator
     * @return
     */
    QPrimaryKeyBuilder addPrimaryKeyGenerator(PKGenerator<?> pkGenerator);

    /**
     * back to Table Builder
     *
     * @return Table Builder
     */
    QTableBuilder endPrimaryKey();
}
