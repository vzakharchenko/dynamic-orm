package com.github.vzakharchenko.dynamic.orm.core.query.crud;

import com.github.vzakharchenko.dynamic.orm.core.DMLModel;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.QueryContextImpl;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by vzakharchenko on 18.09.15.
 */
public class CrudBuilderImpl<MODEL extends DMLModel> implements CrudBuilder<MODEL> {

    private final QueryContextImpl queryContext;

    private final Class<MODEL> modelClass;

    private final RelationalPath<?> qTable;
    private final AfterModify afterModify;

    private PKGenerator pkGenerator;

    private Path<?> versionColumnPath;

    protected CrudBuilderImpl(Class<MODEL> modelClass, RelationalPath<?> qTable,
                              QueryContextImpl queryContext) {
        this.modelClass = modelClass;
        this.qTable = qTable;
        this.queryContext = queryContext;
        versionColumnPath = this.queryContext.getVersionColumn(qTable, modelClass);
        this.afterModify = new AfterModifyImpl(qTable, modelClass, queryContext);
    }


    @Override
    public UpdateModelBuilder<MODEL> updateBuilder() {
        return new UpdateModelBuilderImpl(qTable, queryContext,
                versionColumnPath, modelClass);
    }

    @Override
    public CrudBuilder<MODEL> primaryKeyGenerator(PKGenerator pkGenerator0) {
        this.pkGenerator = pkGenerator0;
        return this;
    }

    @Override
    public CrudBuilder<MODEL> versionColumn(Path<?> versionColumn0) {
        queryContext.validateVersionColumn(versionColumn0);
        RelationalPath<?> qTable0 = ModelHelper.getQTable(versionColumn0);
        Assert.isTrue(Objects.equals(qTable0, this.qTable), "expected " +
                this.qTable + " but found " + qTable0);
        this.versionColumnPath = versionColumn0;
        return this;
    }

    @Override
    public final Long insert(MODEL... models) {
        return insert(Arrays.asList(models));
    }

    @Override
    public Long insert(List<MODEL> models) {
        return new InsertBuilderImpl<>(modelClass, qTable, queryContext,
                afterModify, versionColumnPath, pkGenerator).insert(models);
    }

    @Override
    public DeleteModelBuilder<MODEL> delete(MODEL model) {
        return new DeleteBuilderImpl<>(modelClass, qTable, queryContext, versionColumnPath)
                .delete(model);
    }

    @Override
    public Long deleteModelByIds(MODEL... models) {
        return deleteModelByIds(Arrays.asList(models));
    }

    @Override
    public Long deleteModelByIds(List<MODEL> models) {
        return new DeleteBuilderImpl<>(modelClass, qTable, queryContext, versionColumnPath)
                .deleteModelByIds(models);
    }

    @Override
    public Long softDeleteModelByIds(MODEL... models) {
        return softDeleteModelByIds(Arrays.asList(models));
    }

    @Override
    public Long softDeleteModelByIds(List<MODEL> models) {
        return new DeleteBuilderImpl<>(modelClass, qTable, queryContext, versionColumnPath)
                .softDeleteModelByIds(models);
    }

    @Override
    public Long softDeleteByIds(Serializable... ids) {
        return softDeleteByIds(Arrays.asList(ids));
    }

    @Override
    public Long softDeleteByIds(List<Serializable> ids) {
        return new DeleteBuilderImpl<>(modelClass, qTable, queryContext, versionColumnPath)
                .softDeleteByIds(ids);
    }

    @Override
    public Long deleteByIds(Serializable... ids) {
        return new DeleteBuilderImpl<>(modelClass, qTable, queryContext, versionColumnPath)
                .deleteByIds(ids);
    }

    @Override
    public Long deleteByIds(List<Serializable> ids) {
        return new DeleteBuilderImpl<>(modelClass, qTable, queryContext, versionColumnPath)
                .deleteByIds(ids);
    }
}
