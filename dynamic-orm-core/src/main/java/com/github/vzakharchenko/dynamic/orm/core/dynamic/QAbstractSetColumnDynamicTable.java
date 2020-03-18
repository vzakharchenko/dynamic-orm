package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QNumberColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QSizeColumn;
import liquibase.database.Database;
import liquibase.datatype.DatabaseDataType;
import liquibase.datatype.core.*;

import java.util.Date;


/**
 *
 */
public abstract class QAbstractSetColumnDynamicTable<DYNAMIC_TABLE extends QAbstractSetColumnDynamicTable>
        extends QAbstractColumnDynamicTable<DYNAMIC_TABLE> {

    protected QAbstractSetColumnDynamicTable(String tableName) {
        super(tableName);
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


}
