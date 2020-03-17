package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QNumberColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumn;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.*;
import liquibase.database.Database;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.core.*;
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
    private final Map<String, Path<?>> removedColumns = new LinkedHashMap<>();

    protected final Map<Path<?>, ColumnMetaDataInfo> columnMetaDataInfoMap = new HashMap<>();


    protected QAbstractDynamicTable(String tableName) {
        super(Object.class, PathMetadataFactory.forVariable(tableName), "", tableName);
    }

    protected DYNAMIC_TABLE createStringColumn(
            Database database, QSizeColumn sizeColumn) {
        DatabaseDataType databaseDataType = new VarcharType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(createString(sizeColumn.columnName()),
                databaseDataType.getType(), sizeColumn));
    }

    protected DYNAMIC_TABLE createCharColumn(
            Database database, QSizeColumn sizeColumn) {
        DatabaseDataType databaseDataType = new CharType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(createString(sizeColumn.columnName()),
                databaseDataType.getType(), sizeColumn));
    }

    protected DYNAMIC_TABLE createClobColumn(
            Database database, QSizeColumn sizeColumn) {
        DatabaseDataType databaseDataType = new ClobType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(createString(sizeColumn.columnName()),
                databaseDataType.getType(), sizeColumn));
    }

    protected DYNAMIC_TABLE createBooleanColumn(
            Database database, QColumn column) {
        DatabaseDataType databaseDataType = new BooleanType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(createBoolean(column.columnName()),
                databaseDataType.getType(), 1, column));
    }


    protected DYNAMIC_TABLE createBlobColumn(
            Database database, QSizeColumn sizeColumn) {
        DatabaseDataType dataType = new BlobType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(
                createSimple(sizeColumn.columnName(), byte[].class),
                dataType.getType(), sizeColumn));
    }

    protected DYNAMIC_TABLE createNumberColumn(
            Database database, QNumberColumn numberColumn) {
        DatabaseDataType databaseDataType = new NumberType().toDatabaseDataType(database);
        return addColumn(new NumberColumnMetaDataInfo(
                createNumber(numberColumn.columnName(),
                        numberColumn.numberClass()),
                databaseDataType.getType(), numberColumn));
    }

    protected DYNAMIC_TABLE createDateColumn(
            Database database, QSizeColumn sizeColumn) {
        DatabaseDataType dataType = new DateType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(
                createDate(sizeColumn.columnName(), Date.class),
                dataType.getType(), sizeColumn));
    }

    protected DYNAMIC_TABLE createDateTimeColumn(
            Database database, QSizeColumn sizeColumn) {
        DatabaseDataType dataType = new TimestampType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(
                createDateTime(sizeColumn.columnName(), Date.class),
                dataType.getType(), sizeColumn));
    }

    protected DYNAMIC_TABLE createTimeColumn(
            Database database, QSizeColumn sizeColumn) {
        DatabaseDataType dataType = new TimeType().toDatabaseDataType(database);
        return addColumn(new SizeColumnMetaDataInfo(
                createTime(sizeColumn.columnName(), Date.class),
                dataType.getType(), sizeColumn));
    }


    protected DYNAMIC_TABLE addPrimaryKey(
            Path path) {
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
            updatePrimaryKey(localColumns.toArray(new Path[0]), path);
        }
        return (DYNAMIC_TABLE) this;
    }

    protected PrimaryKey<Object> updatePrimaryKey(Path[] columnArray,
                                                  Path column) {
        return createPrimaryKey(ArrayUtils.add(columnArray, column));
    }


    protected DYNAMIC_TABLE addPrimaryKey(String columnName) {
        Assert.hasText(columnName);
        Path<?> pkColumn = columns.get(StringUtils.upperCase(columnName));
        return addPrimaryKey(pkColumn);
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
        return (DYNAMIC_TABLE) this;
    }


    protected DYNAMIC_TABLE addColumn(
            ColumnMetaDataInfo columnMetaDataInfo) {
        Assert.notNull(columnMetaDataInfo);
        addMetadata(columnMetaDataInfo);
        Path column = columnMetaDataInfo.getColumn();
        if (BooleanUtils.isTrue(columnMetaDataInfo.isPrimaryKey())) {
            addPrimaryKey(column);
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

    public Path<?> getColumnByName(String columnName) {
        return columns.get(StringUtils.upperCase(columnName));
    }

    public <T> SimpleExpression<T> getColumnByName(String columnName, Class<T> tClass) {
        Path<?> column = getColumnByName(columnName);
        if (column == null) {
            throw new IllegalStateException("column " + columnName +
                    " is not found in table " + getTableName());
        }
        Assert.isTrue(tClass.isAssignableFrom(column.getType()));
        return (SimpleExpression<T>) column;
    }

    public StringPath getStringColumnByName(String columnName) {
        return (StringPath) getColumnByName(columnName, String.class);
    }

    public StringPath getCharColumnByName(String columnName) {
        return getStringColumnByName(columnName);
    }

    public StringPath getClobColumnByName(String columnName) {
        return getStringColumnByName(columnName);
    }

    public BooleanPath getBooleanColumnByName(String columnName) {
        return (BooleanPath) getColumnByName(columnName, Boolean.class);
    }

    public SimplePath<byte[]> getBlobColumnByName(String columnName) {
        return (SimplePath<byte[]>) getColumnByName(columnName, byte[].class);
    }

    public DatePath<Date> getDateColumnByName(String columnName) {
        return (DatePath<Date>) getColumnByName(columnName, Date.class);
    }

    public DateTimePath<Date> getDateTimeColumnByName(String columnName) {
        return (DateTimePath<Date>) getColumnByName(columnName, Date.class);
    }

    public TimePath<Date> getTimeColumnByName(String columnName) {
        return (TimePath<Date>) getColumnByName(columnName, Date.class);
    }

    public <T extends Number & Comparable<?>> NumberPath<T> getNumberColumnByName(
            String columnName, Class<T> tClass) {
        return (NumberPath<T>) getColumnByName(columnName, tClass);
    }

    public <T extends Number & Comparable<?>> NumberPath<T> getNumberColumnByName(
            String columnName) {
        return (NumberPath) getColumnByName(columnName, Number.class);
    }


    public List<String> deletedColumns() {
        return new ArrayList<>(removedColumns.keySet());
    }
}
