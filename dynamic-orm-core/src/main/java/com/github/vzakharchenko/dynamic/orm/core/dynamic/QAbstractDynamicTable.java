package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.*;
import com.querydsl.sql.ColumnMetadata;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.RelationalPath;
import com.querydsl.sql.RelationalPathBase;
import liquibase.database.Database;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.core.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;

import java.util.*;


/**
 *
 */
public abstract class QAbstractDynamicTable<DYNAMIC_TABLE extends QAbstractDynamicTable>
        extends RelationalPathBase<Object> {

    private final Map<String, Path<?>> columns = new LinkedHashMap<>();

    private final Map<Path<?>, ColumnMetaDataInfo> columnMetaDataInfoMap = new HashMap<>();


    protected QAbstractDynamicTable(String tableName) {
        super(Object.class, PathMetadataFactory.forVariable(tableName), "", tableName);
    }

    protected DYNAMIC_TABLE createStringColumn(
            Database database, String columnName, int size, boolean notNull) {
        DatabaseDataType databaseDataType = new VarcharType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createString(columnName),
                databaseDataType.getType(), size, !notNull));
    }

    protected DYNAMIC_TABLE createCharColumn(
            Database database, String columnName, int size, boolean notNull) {
        DatabaseDataType databaseDataType = new CharType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createString(columnName),
                databaseDataType.getType(), size, !notNull));
    }

    protected DYNAMIC_TABLE createClobColumn(
            Database database, String columnName, int size, boolean notNull) {
        DatabaseDataType databaseDataType = new ClobType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createString(columnName),
                databaseDataType.getType(), size, !notNull));
    }

    protected DYNAMIC_TABLE createBooleanColumn(
            Database database, String columnName, boolean notNull) {
        DatabaseDataType databaseDataType = new BooleanType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createBoolean(columnName),
                databaseDataType.getType(), 1, !notNull));
    }


    protected DYNAMIC_TABLE createBlobColumn(
            Database database, String columnName, int size, boolean notNull) {
        DatabaseDataType dataType = new BlobType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createSimple(columnName, byte[].class),
                dataType.getType(), size, !notNull));
    }

    protected <T extends Number & Comparable<?>> DYNAMIC_TABLE createNumberColumn(
            Database database, String columnName, Class<T> typeClass, Integer size,
            Integer decimalDigits, boolean notNull) {
        DatabaseDataType databaseDataType = new NumberType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createNumber(columnName, typeClass),
                databaseDataType.getType(), size, !notNull, decimalDigits));
    }

    protected DYNAMIC_TABLE createDateColumn(
            Database database, String columnName, boolean notNull) {
        DatabaseDataType databaseDataType = new DateType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createDate(columnName, Date.class),
                databaseDataType.getType(), 26, !notNull));
    }

    protected DYNAMIC_TABLE createDateTimeColumn(
            Database database, String columnName, boolean notNull) {
        DatabaseDataType databaseDataType = new TimestampType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createDateTime(columnName, Date.class),
                databaseDataType.getType(), 26, !notNull));
    }

    protected DYNAMIC_TABLE createTimeColumn(
            Database database, String columnName, boolean notNull) {
        DatabaseDataType databaseDataType = new TimeType().toDatabaseDataType(database);
        return addColumn(new ColumnMetaDataInfo(createTime(columnName, Date.class),
                databaseDataType.getType(), 26, !notNull));
    }


    protected DYNAMIC_TABLE addPrimaryKey(
            Path path) {
        Assert.notNull(path);
        RelationalPath<?> qTable = ModelHelper.getQTable(path);
        Assert.isTrue(Objects.equals(this, qTable));
        ColumnMetadata metadata = this.getMetadata(path);
        Assert.notNull(metadata);
        Assert.isTrue(!metadata.isNullable());
        createPrimaryKey(path);
        return (DYNAMIC_TABLE) this;
    }

    protected DYNAMIC_TABLE addPrimaryKey(String columnName) {
        Assert.hasText(columnName);
        Path<?> pkColumn = columns.get(StringUtils.upperCase(columnName));
        return addPrimaryKey(pkColumn);
    }

    protected DYNAMIC_TABLE addForeignKey(
            Path localColumn, RelationalPath<?> remoteQTable, Path remotePrimaryKey) {
        Assert.notNull(localColumn);
        RelationalPath<?> qTable = ModelHelper.getQTable(localColumn);
        Assert.isTrue(Objects.equals(this, qTable));
        ForeignKey foreignKey = new ForeignKey(remoteQTable, localColumn,
                remotePrimaryKey.getMetadata().getName());
        getForeignKeys().add(foreignKey);
        return (DYNAMIC_TABLE) this;
    }

    protected DYNAMIC_TABLE addForeignKey(
            String localColumnName, RelationalPath<?> remoteQTable, Path remotePrimaryKey) {
        Assert.hasText(localColumnName);
        Path<?> fkColumn = columns.get(StringUtils.upperCase(localColumnName));
        return addForeignKey(fkColumn, remoteQTable, remotePrimaryKey);
    }

    protected DYNAMIC_TABLE addForeignKey(
            String localColumnName, RelationalPath<?> remoteQTable) {
        Assert.hasText(localColumnName);
        Path<?> fkColumn = columns.get(StringUtils.upperCase(localColumnName));
        return addForeignKey(fkColumn, remoteQTable, ModelHelper
                .getPrimaryKeyColumn(remoteQTable));
    }

    private DYNAMIC_TABLE addColumn(
            ColumnMetaDataInfo columnMetaDataInfo) {
        Assert.notNull(columnMetaDataInfo);
        addMetadata(columnMetaDataInfo);
        Path column = columnMetaDataInfo.getColumn();
        columns.put(ModelHelper.getColumnRealName(column), column);
        columnMetaDataInfoMap.put(column, columnMetaDataInfo);
        Assert.notNull(add(column));
        return (DYNAMIC_TABLE) this;
    }

    private void addMetadata(
            ColumnMetaDataInfo columnMetaDataInfo) {
        Assert.notNull(columnMetaDataInfo);
        Path column = columnMetaDataInfo.getColumn();
        ColumnMetadata columnMetadata = ColumnMetadata
                .named(StringUtils.upperCase(column.getMetadata().getName()))
                .withSize(columnMetaDataInfo.getSize());
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

        if (value != null && !value.getClass().isAssignableFrom(column.getType())) {
            throw new IllegalStateException(value + " has wrong type: expected " +
                    column.getType() + " but found " + value.getClass());
        }
    }

    public <T> SimpleExpression<T> getColumnByName(String columnName, Class<T> tClass) {
        Path<?> column = columns.get(StringUtils.upperCase(columnName));
        if (column == null) {
            throw new IllegalStateException("column " + columnName +
                    " is not found in table " + getTableName());
        }
        Assert.isTrue(tClass.isAssignableFrom(column.getType()));
        return (SimpleExpression<T>) column;
    }

    public StringPath getStringColumnByName(String columnName) {
        SimpleExpression<String> column = getColumnByName(columnName, String.class);
        return (StringPath) column;
    }

    public StringPath getCharColumnByName(String columnName) {
        return getStringColumnByName(columnName);
    }

    public StringPath getClobColumnByName(String columnName) {
        return getStringColumnByName(columnName);
    }

    public BooleanPath getBooleanColumnByName(String columnName) {
        SimpleExpression<Boolean> column = getColumnByName(columnName, Boolean.class);
        return (BooleanPath) column;
    }

    public SimplePath<byte[]> getBlobColumnByName(String columnName) {
        SimpleExpression<byte[]> column = getColumnByName(columnName, byte[].class);
        return (SimplePath<byte[]>) column;
    }

    public DatePath<Date> getDateColumnByName(String columnName) {
        SimpleExpression<Date> column = getColumnByName(columnName, Date.class);
        return (DatePath<Date>) column;
    }

    public DateTimePath<Date> getDateTimeColumnByName(String columnName) {
        SimpleExpression<Date> column = getColumnByName(columnName, Date.class);
        return (DateTimePath<Date>) column;
    }

    public TimePath<Date> getTimeColumnByName(String columnName) {
        SimpleExpression<Date> column = getColumnByName(columnName, Date.class);
        return (TimePath<Date>) column;
    }

    public <T extends Number & Comparable<?>> NumberPath<T> getNumberColumnByName(
            String columnName, Class<T> tClass) {
        SimpleExpression<T> column = getColumnByName(columnName, tClass);
        return (NumberPath<T>) column;
    }

    public <T extends Number & Comparable<?>> NumberPath<T> getNumberColumnByName(
            String columnName) {
        SimpleExpression<Number> column = getColumnByName(columnName, Number.class);
        return (NumberPath) column;
    }


}
