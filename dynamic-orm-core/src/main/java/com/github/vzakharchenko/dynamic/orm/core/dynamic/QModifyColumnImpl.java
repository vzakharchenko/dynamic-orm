package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QModifyColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumn;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.column.QTableColumnContext;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadataFactory;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import org.springframework.util.Assert;

public class QModifyColumnImpl implements QModifyColumn {

    private final QTableColumnContext qTableColumnContext;
    private final QDynamicTable dynamicTable;

    public QModifyColumnImpl(QTableColumnContext qTableColumnContext,
                             QDynamicTable dynamicTable) {
        this.qTableColumnContext = qTableColumnContext;
        this.dynamicTable = dynamicTable;
    }

    @Override
    public QModifyColumn removeColumn(String... columns) {
        Assert.notNull(columns, "columns are null");
        for (String column : columns) {
            dynamicTable.removeColumn(column);
        }
        return this;
    }

    private ModifyColumnMetaDataInfo getMetadata(Path<?> column) {
        ModifyColumnMetaDataInfoImpl modifyColumnMetaDataInfo =
                new ModifyColumnMetaDataInfoImpl(dynamicTable.getMetaInfo(column));
        dynamicTable.addColumn(modifyColumnMetaDataInfo);
        return modifyColumnMetaDataInfo;
    }

    private ModifyColumnMetaDataInfo getMetadata(String column) {
        Path<?> columnByName = dynamicTable.getColumnByName(column);
        Assert.notNull(columnByName, "Column " + column + " does not exists");
        return getMetadata(columnByName);
    }

    @Override
    public QModifyColumn size(String column, int newSize) {
        getMetadata(column).setSize(newSize);
        return this;
    }

    @Override
    public QModifyColumn decimalDigits(String column, int newDecimalDigits) {
        getMetadata(column).setDecimalDigits(newDecimalDigits);
        return this;
    }

    @Override
    public QModifyColumn nullable(String column) {
        getMetadata(column).setNullable(Boolean.TRUE);
        return this;
    }

    @Override
    public QModifyColumn notNull(String column) {
        getMetadata(column).setNullable(Boolean.FALSE);
        return this;
    }

    @Override
    public <T extends Number & Comparable<?>> QModifyColumn changeNumberType(String column,
                                                                             Class<T> tclass) {
        ModifyColumnMetaDataInfo metadata = getMetadata(column);
        NumberPath<?> columnByName = dynamicTable.getNumberColumnByName(column);
        NumberPath<T> newColumn = Expressions.numberPath(tclass,
                PathMetadataFactory.forProperty(dynamicTable,
                        ModelHelper.getColumnName(columnByName)));
        metadata.setColumn(newColumn);
        dynamicTable.addColumn((ColumnMetaDataInfo) metadata);
        return this;
    }

    @Override
    public QTableColumn finish() {
        return qTableColumnContext;
    }
}
