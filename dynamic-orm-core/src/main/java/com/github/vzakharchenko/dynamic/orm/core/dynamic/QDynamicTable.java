package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDeleteFactory;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

/**
 *
 */
public final class QDynamicTable extends QAbstractDynamicTable<QDynamicTable> {

    private final Map<Serializable, Serializable> customFields = new HashMap<>();
    private PKGenerator<?> pkGenerator;
    private Path<?> versionColumn;
    private SoftDelete<?> softDelete;
    private List<IndexData> indexDatas = new ArrayList<>();

    protected QDynamicTable(String tableName) {
        super(StringUtils.upperCase(tableName));
    }

    // CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) { //NOPMD
        if (o instanceof QDynamicTable) {
            if (this == o) {
                return true;
            }
            if (getClass() != o.getClass()) {
                return false;
            }
            if (!super.equals(o)) {
                return false;
            }

            QDynamicTable that = (QDynamicTable) o;

            return StringUtils.equalsIgnoreCase(this.getTableName(), that.getTableName());
        } else {
            return super.equals(o);
        }
    }
    // CHECKSTYLE:ON

    protected QDynamicTable addPKGenerator(PKGenerator<?> pkGenerator0) {
        Path<?> primaryKeyColumn = ModelHelper.getPrimaryKeyColumn(this);
        if (primaryKeyColumn == null) {
            throw new IllegalStateException("primary key is not found: " + this);
        }
        Assert.isTrue(pkGenerator0.getTypedClass().isAssignableFrom(primaryKeyColumn.getType()));
        this.pkGenerator = pkGenerator0;
        return this;
    }

    protected QDynamicTable addVersionColumn(Path<?> versionColumn0) {
        if (versionColumn0 == null) {
            throw new IllegalStateException("column is not found: " + this);
        }
        RelationalPath<?> qTable = ModelHelper.getQTable(versionColumn0);
        if (ObjectUtils.notEqual(qTable, this)) {
            throw new IllegalStateException("Wrong column " + versionColumn0);
        }
        this.versionColumn = versionColumn0;
        return this;
    }

    protected QDynamicTable addVersionColumn(String columnName) {
        Path versionColumn0 = ModelHelper.getColumnByName(this,
                StringUtils.upperCase(columnName));
        if (versionColumn0 == null) {
            throw new IllegalStateException("column " + columnName + " is not found: " + this);
        }
        return addVersionColumn(versionColumn0);
    }

    protected <TYPE extends Serializable> QDynamicTable addSoftDeleteColumn(
            Path<TYPE> softDelete0, TYPE value, TYPE defaultValue) {
        if (softDelete0 == null) {
            throw new IllegalStateException("column is not found: " + this);
        }
        RelationalPath<?> qTable = ModelHelper.getQTable(softDelete0);
        if (ObjectUtils.notEqual(qTable, this)) {
            throw new IllegalStateException("Wrong column " + softDelete0);
        }
        this.softDelete = SoftDeleteFactory.createSoftDelete(softDelete0, value, defaultValue);
        return this;
    }

    protected QDynamicTable addSoftDeleteColumn(
            String columnName, Serializable value, Serializable defaultValue) {
        Path<Serializable> softDeleteColumn = ModelHelper
                .getColumnByName(this, StringUtils.upperCase(columnName));
        if (softDeleteColumn == null) {
            throw new IllegalStateException("column " + columnName + " is not found: " + this);
        }
        RelationalPath<?> qTable = ModelHelper.getQTable(softDeleteColumn);
        if (ObjectUtils.notEqual(qTable, this)) {
            throw new IllegalStateException("Wrong column " + softDeleteColumn);
        }
        if (value != null && !value.getClass().isAssignableFrom(softDeleteColumn.getType())) {
            throw new IllegalStateException("wrong value type: expected " + softDeleteColumn
                    .getType() + " but found " + value.getClass());
        }
        if (defaultValue != null && !defaultValue.getClass().isAssignableFrom(softDeleteColumn
                .getType())) {
            throw new IllegalStateException("wrong defaultValue type: expected " +
                    softDeleteColumn.getType() + " but found " + defaultValue.getClass());
        }
        this.softDelete = SoftDeleteFactory.createSoftDelete(
                softDeleteColumn, value, defaultValue);
        return this;
    }

    protected QDynamicTable addIndex(Path<?> column, boolean unique) {
        if (column == null) {
            throw new IllegalStateException("column is not found: " + this);
        }
        RelationalPath<?> qTable = ModelHelper.getQTable(column);
        if (ObjectUtils.notEqual(qTable, this)) {
            throw new IllegalStateException("Wrong column " + column);
        }
        indexDatas.add(new IndexData(column, unique));
        return this;
    }

    protected QDynamicTable addIndex(String columnName, boolean unique) {
        if (columnName == null) {
            throw new IllegalStateException("columnName " + " is Empty: " + this);
        }
        return addIndex(ModelHelper.getColumnByName(this,
                StringUtils.upperCase(columnName)), unique);
    }

    protected PKGenerator<?> getPkGenerator() {
        return pkGenerator;
    }

    protected Path<?> getVersionColumn() {
        return versionColumn;
    }

    protected SoftDelete<?> getSoftDelete() {
        return softDelete;
    }

    protected List<IndexData> getIndexDatas() {
        return Collections
                .unmodifiableList(indexDatas);
    }

    protected QDynamicTable registerCustomFields(Serializable key, Serializable value) {
        customFields.put(key, value);
        return this;
    }

    public Map<Serializable, Serializable> getCustomFields() {
        return ImmutableMap.copyOf(customFields);
    }
}
