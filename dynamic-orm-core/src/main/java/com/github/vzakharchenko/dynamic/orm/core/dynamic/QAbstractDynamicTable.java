package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.sql.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;


/**
 *
 */
public abstract class QAbstractDynamicTable<DYNAMIC_TABLE extends QAbstractDynamicTable>
        extends RelationalPathBase<Object> {

    protected final Map<String, Path<?>> columns = new LinkedHashMap<>();
    protected final Map<Path<?>, ColumnMetaDataInfo> columnMetaDataInfoMap = new HashMap<>();
    private final Map<String, Path<?>> removedColumns = new LinkedHashMap<>();
    private final List<ForeignKey<?>> removedForeignKeys = new ArrayList<>();

    protected QAbstractDynamicTable(String tableName) {
        super(Object.class, PathMetadataFactory.forVariable(tableName), "", tableName);
    }

    protected DYNAMIC_TABLE modifyPrimaryKey(
            Path path, boolean remove) {
        Assert.notNull(path);
        RelationalPath<?> qTable = ModelHelper.getQTable(path);
        Assert.isTrue(Objects.equals(this, qTable));
        ColumnMetadata metadata = this.getMetadata(path);
        Assert.notNull(metadata);
        Assert.isTrue(!metadata.isNullable());
        PrimaryKey<Object> primaryKey = getPrimaryKey();
        if (primaryKey == null) {
            createPrimaryKey(path);
        } else {
            List<? extends Path<?>> localColumns = primaryKey.getLocalColumns();
            updatePrimaryKey(localColumns.toArray(new Path[0]), path, remove);
        }
        return (DYNAMIC_TABLE) this;
    }

    protected PrimaryKey<Object> updatePrimaryKey(Path[] columnArray,
                                                  Path column,
                                                  boolean remove) {
        return createPrimaryKey(remove ? ArrayUtils.removeElement(columnArray, column) :
                ArrayUtils.add(columnArray, column));
    }


    protected DYNAMIC_TABLE addPrimaryKey(String columnName) {
        Assert.hasText(columnName);
        Path<?> pkColumn = columns.get(StringUtils.upperCase(columnName));
        return modifyPrimaryKey(pkColumn, false);
    }

    protected DYNAMIC_TABLE removePrimaryKey(String columnName) {
        Assert.hasText(columnName);
        Path<?> pkColumn = columns.get(StringUtils.upperCase(columnName));
        Assert.notNull(pkColumn, "Column " + columnName + " does not exist.");
        ModifyColumnMetaDataInfo metaInfo = new ModifyColumnMetaDataInfoImpl(getMetaInfo(pkColumn));
        metaInfo.setPrimaryKey(Boolean.FALSE);
        columnMetaDataInfoMap.put(pkColumn, metaInfo);
        return modifyPrimaryKey(pkColumn, true);
    }

    protected DYNAMIC_TABLE addForeignKey(
            List<Path<?>> localColumns,
            RelationalPath<?> remoteQTable,
            List<Path<?>> remoteKeys) {
        Assert.notNull(localColumns, "localColumns is null");
        Assert.isTrue(!localColumns.isEmpty(), "localColumns are empty");
        Assert.isTrue(Objects.equals(remoteKeys.size(), localColumns.size()),
                "localColumns and remoteKeys have different size");
        for (Path localColumn : localColumns) {
            RelationalPath<?> qTable = ModelHelper.getQTable(localColumn);
            Assert.isTrue(Objects.equals(this, qTable));
        }
        ForeignKey foreignKey = new ForeignKey(remoteQTable,
                ImmutableList.copyOf(localColumns),
                ImmutableList.copyOf(remoteKeys.stream().map(
                        ModelHelper::getColumnRealName)
                        .collect(Collectors.toList())));
        getForeignKeys().add(foreignKey);
        removedForeignKeys.remove(foreignKey);
        return (DYNAMIC_TABLE) this;
    }


    protected DYNAMIC_TABLE addColumn(
            ColumnMetaDataInfo columnMetaDataInfo) {
        Assert.notNull(columnMetaDataInfo);
        addMetadata(columnMetaDataInfo);
        Path column = columnMetaDataInfo.getColumn();
        if (BooleanUtils.isTrue(columnMetaDataInfo.isPrimaryKey())) {
            modifyPrimaryKey(column, false);
        }
        columns.put(ModelHelper.getColumnRealName(column), column);
        columnMetaDataInfoMap.put(column, columnMetaDataInfo);
        Assert.notNull(add(column));
        return (DYNAMIC_TABLE) this;
    }

    protected DYNAMIC_TABLE removeColumn(String name) {
        Path<?> column = getColumnByName(name);
        Assert.notNull(column, "Column " + name + " does not exists");
        String columnName = StringUtils.upperCase(name);
        columns.remove(columnName);
        removedColumns.put(columnName, column);
        return (DYNAMIC_TABLE) this;
    }

    private void addMetadata(
            ColumnMetaDataInfo columnMetaDataInfo) {
        Assert.notNull(columnMetaDataInfo);
        Path column = columnMetaDataInfo.getColumn();
        ColumnMetadata columnMetadata = ColumnMetadata
                .named(StringUtils.upperCase(column.getMetadata().getName()));
        if (columnMetaDataInfo.getSize() != null) {
            columnMetadata = columnMetadata.withSize(columnMetaDataInfo.getSize());
        }
        if (columnMetaDataInfo.getDecimalDigits() != null) {
            columnMetadata = columnMetadata.withDigits(columnMetaDataInfo.getDecimalDigits());
        }
        if (!columnMetaDataInfo.isNullable()) {
            columnMetadata = columnMetadata.notNull();
        }
        removedColumns.remove(columnMetadata.getName());
        addMetadata(column, columnMetadata);
    }

    public ColumnMetaDataInfo getMetaInfo(Path<?> column) {
        return columnMetaDataInfoMap.get(column);
    }

    public void checkColumn(String columnName, Object value) {
        Path<?> column = columns.get(StringUtils.upperCase(columnName));
        if (column == null) {
            throw new IllegalStateException(columnName + " is not found :" + this);
        }
        if (value instanceof Date) {
            return;
        }

        if (value != null && !column.getType().isAssignableFrom(value.getClass())) {
            throw new IllegalStateException(value + " has wrong type: expected " +
                    column.getType() + " but found " + value.getClass());
        }
    }

    public abstract Path<?> getColumnByName(String columnName);

    public List<String> deletedColumns() {
        return new ArrayList<>(removedColumns.keySet());
    }

    public List<ForeignKey<?>> deletedForeignKeys() {
        return this.removedForeignKeys;
    }

    protected abstract void init();

    public void reInit() {
        deletedColumns().clear();
        deletedForeignKeys().clear();
        init();
    }

    public void removeForeignKey(List<Path<?>> localColumns) {
        List<ForeignKey<?>> foreignKeys = getForeignKeys().stream().filter(
                (Predicate<ForeignKey<?>>) foreignKey ->
                        !CollectionUtils.isEqualCollection(foreignKey.getLocalColumns(),
                                localColumns)).collect(Collectors.toList());
        List<ForeignKey<?>> foreignKeysToDelete = getForeignKeys().stream().filter(
                (Predicate<ForeignKey<?>>) foreignKey ->
                        CollectionUtils.isEqualCollection(foreignKey.getLocalColumns(),
                                localColumns)).collect(Collectors.toList());
        this.removedForeignKeys.addAll(foreignKeysToDelete);
        getForeignKeys().clear();
        getForeignKeys().addAll(foreignKeys);
    }
}
