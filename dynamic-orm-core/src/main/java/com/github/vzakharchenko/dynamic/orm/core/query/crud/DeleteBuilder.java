package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.QueryDslModel;
import com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.annotations.Version;

import java.io.Serializable;
import java.util.List;

/**
 * Delete builder
 */
public interface DeleteBuilder<MODEL extends DMLModel> {

    /**
     * delete from model
     * <p>
     * ARE SUPPORTED MODELS WITHOUT PRIMARY KEYS
     *
     * @param model
     * @return DeleteModelBuilder
     */
    DeleteModelBuilder<MODEL> delete(MODEL model);

    /**
     * delete rows from table.
     * <p>
     * Attention! the table should have a Primary Key
     * <p>
     * Example: delete from table  where id=?
     *
     * @param models data model(dml model) with annotation @QueryDslModel
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see CrudBuilder
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    Long deleteModelByIds(MODEL... models);

    /**
     * delete rows from table.
     * <p>
     * Attention! the table should have a Primary Key
     * <p>
     * Example: delete from table  where id=?
     *
     * @param models data model(dml model) with annotation @QueryDslModel
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see CrudBuilder
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    Long deleteModelByIds(List<MODEL> models);

    /**
     * It is used if you want to not remove the data physically.
     *
     * @param models data model(dml model) with annotation @QueryDslModel
     * @return affected rows
     * <p>
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     * @see com.github.vzakharchenko.dynamic.orm.core.annotations.SoftDelete
     * Attention! the table should have a Primary Key
     * Attention! soft deleted rows are not cached
     * <p>
     * Example:  uptade Table set(status =-1) where id=?
     */
    Long softDeleteModelByIds(MODEL... models);

    /**
     * It is used if you want to not remove the data physically.
     *
     * @param models data model(dml model) with annotation @QueryDslModel
     * @return affected rows
     * <p>
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     * @see SoftDelete
     * Attention! the table should have a Primary Key
     * Attention! soft deleted rows are not cached
     * <p>
     * Example:  uptade Table set(status =-1) where id=?
     */
    Long softDeleteModelByIds(List<MODEL> models);

    /**
     * It is used if you want to not remove the data physically.
     *
     * @param ids list of primary keys
     * @return affected rows
     * <p>
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     * @see SoftDelete
     * Attention! the table should have a Primary Key
     * Attention! soft deleted rows are not cached
     * <p>
     * Example:  uptade Table set(status =-1) where id=?
     */
    Long softDeleteByIds(Serializable... ids);

    /**
     * It is used if you want to not remove the data physically.
     *
     * @param ids list of primary keys
     * @return affected rows
     * <p>
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     * @see SoftDelete
     * Attention! the table should have a Primary Key
     * Attention! soft deleted rows are not cached
     * <p>
     * Example:  uptade Table set(status =-1) where id=?
     */
    Long softDeleteByIds(List<Serializable> ids);

    /**
     * delete rows from table.
     * <p>
     * Attention! the table should have a Primary Key
     * <p>
     * Example: delete from table  where id=?
     *
     * @param ids list of primary keys
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see CrudBuilder
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    Long deleteByIds(Serializable... ids);

    /**
     * delete rows from table.
     * <p>
     * Attention! the table should have a Primary Key
     * <p>
     * Example: delete from table  where id=?
     *
     * @param ids list of primary keys
     * @return affected rows
     * @see QueryDslModel
     * @see Version
     * @see CrudBuilder
     * <p>
     * Attention! If you use the annotation com.github.vzakharchenko.dynamic.orm.core.annotations.Version
     * is it checks the equality of the number of affected rows and the number of models
     */
    Long deleteByIds(List<Serializable> ids);
}
