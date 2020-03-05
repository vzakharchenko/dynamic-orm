package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.pk.*;
import com.querydsl.core.types.Path;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public interface CrudBuilder<MODEL extends DMLModel>
        extends InsertBuilder<MODEL>, DeleteBuilder<MODEL> {
    /**
     * update builder
     * <p>
     * ARE SUPPORTED MODELS WITHOUT PRIMARY KEYS
     *
     * @return UpdateModelBuilder
     */
    UpdateModelBuilder<MODEL> updateBuilder();

    /**
     * add PrimaryKey generator
     *
     * @param pkGenerator
     * @return this
     * @see PKGenerator
     * @see PKGeneratorInteger
     * @see PKGeneratorLong
     * @see PKGeneratorSequence
     * @see PKGeneratorUUID
     * @see PrimaryKeyGenerators
     * @see QueryDslModel
     */
    CrudBuilder<MODEL> primaryKeyGenerator(PKGenerator pkGenerator);


    /**
     * add support Optimistic strategy locking
     * <p>
     * supported Column types: Integer,Long,BigInteger,TimeStamp,Date
     *
     * @param versionColumn column from current QTable
     * @return this
     */
    CrudBuilder<MODEL> versionColumn(Path<?> versionColumn);
}
