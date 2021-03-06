package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
import com.github.vzakharchenko.dynamic.orm.core.pk.PKGenerator;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDelete;
import com.github.vzakharchenko.dynamic.orm.core.query.crud.SoftDeleteFactory;
import com.google.common.collect.Sets;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPath;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.vzakharchenko.dynamic.orm.core.RawModelBuilderImpl.SIZE;

/**
 *
 */
public class QDynamicTable extends QAbstractSetColumnDynamicTable<QDynamicTable> {

    public static final String WRONG_COLUMN = "Wrong column ";
    private final List<IndexData> indexDatas = new ArrayList<>();
    private final List<IndexData> removedIndexList = new ArrayList<>();
    private PKGenerator<?> pkGenerator;
    private Path<?> versionColumn;
    private SoftDelete<?> softDelete;

    protected QDynamicTable(String tableName) {
        super(StringUtils.upperCase(tableName));
    }

    @Override
    protected void init() {
        removedIndexList.clear();
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
        List<? extends Path<?>> columns = PrimaryKeyHelper.getPrimaryKeyColumns(this);
        if (columns.size() > SIZE) {
            throw new IllegalStateException("Composite key does not support: " + this);
        }
        Assert.notNull(pkGenerator0);
        Assert.isTrue(pkGenerator0.getTypedClass().isAssignableFrom(columns.get(0).getType()));
        this.pkGenerator = pkGenerator0;
        return this;
    }

    protected QDynamicTable addVersionColumn(Path<?> versionColumn0) {
        if (versionColumn0 == null) {
            throw new IllegalStateException("column is not found: " + this);
        }
        RelationalPath<?> qTable = ModelHelper.getQTable(versionColumn0);
        if (ObjectUtils.notEqual(qTable, this)) {
            throw new IllegalStateException(WRONG_COLUMN + versionColumn0);
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

    protected QDynamicTable addSoftDeleteColumn(
            String columnName, Serializable value, Serializable defaultValue) {
        Path<Serializable> softDeleteColumn = ModelHelper
                .getColumnByName(this, StringUtils.upperCase(columnName));
        if (softDeleteColumn == null) {
            throw new IllegalStateException("column " + columnName + " is not found: " + this);
        }
        RelationalPath<?> qTable = ModelHelper.getQTable(softDeleteColumn);
        if (ObjectUtils.notEqual(qTable, this)) {
            throw new IllegalStateException(WRONG_COLUMN + softDeleteColumn);
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

    protected QDynamicTable addIndex(List<Path<?>> columns,
                                     boolean unique,
                                     boolean clustered) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalStateException("columns are not found: " + this);
        }
        for (Path<?> column : columns) {
            RelationalPath<?> qTable = ModelHelper.getQTable(column);
            if (ObjectUtils.notEqual(qTable, this)) {
                throw new IllegalStateException(WRONG_COLUMN + column);
            }
        }
        IndexData indexData = new IndexData(columns, unique, clustered);
        indexDatas.add(indexData);
        resetRemovedIndices(indexData);
        return this;
    }


    private void resetRemovedIndices(IndexData indexData) {
        List<IndexData> list = removedIndexList.stream().filter(id ->
                !CollectionUtils.isEqualCollection(
                        Sets.newHashSet(indexData.getColumns()), Sets.newHashSet(id.getColumns())))
                .collect(Collectors.toList());
        updateIndices(removedIndexList, list);
    }

    public PKGenerator<?> getPkGenerator() {
        return pkGenerator;
    }

    public Path<?> getVersionColumn() {
        return versionColumn;
    }

    public SoftDelete<?> getSoftDelete() {
        return softDelete;
    }

    public List<IndexData> getIndexDatas() {
        return Collections
                .unmodifiableList(indexDatas);
    }

    @Override
    public Path<?>[] all() {
        return getColumns().toArray(new Path[0]);
    }

    @Override
    public List<Path<?>> getColumns() {
        return new ArrayList<>(columns.values());
    }

    public void removeIndex(List<Path<?>> localColumns) {
        List<IndexData> list1 = indexDatas.stream().filter(indexData ->
                !CollectionUtils.isEqualCollection(
                        Sets.newHashSet(indexData.getColumns()),
                        Sets.newHashSet(localColumns))).collect(Collectors.toList());

        List<IndexData> list2 = indexDatas.stream().filter(indexData ->
                CollectionUtils.isEqualCollection(
                        Sets.newHashSet(indexData.getColumns()),
                        Sets.newHashSet(localColumns))).collect(Collectors.toList());
        updateIndices(indexDatas, list1);
        removedIndexList.addAll(list2);
    }

    public List<IndexData> removedIndices() {
        return this.removedIndexList;
    }

    private void updateIndices(List<IndexData> origin, List<IndexData> list) {
        origin.clear();
        origin.addAll(list);
    }
}
