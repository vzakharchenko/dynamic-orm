package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SequanceName;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.annotations.Version;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;

import java.util.List;

/**
 * Created by vzakharchenko on 18.09.15.
 */

/**
 * insert into builder
 *
 * @param <MODEL>
 */
public interface InsertBuilder<MODEL extends DMLModel> {
    /**
     * insert into table.
     *
     * @param models data model(dml model) with annotation @QueryDslModel
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete
     * @see SequanceName
     * @see PKGenerator
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    Long insert(MODEL... models);

    /**
     * insert into table.
     *
     * @param models data model(dml model) with annotation @QueryDslModel
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see SoftDelete
     * @see SequanceName
     * @see PKGenerator
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    Long insert(List<MODEL> models);
}
