package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Path;
import com.querydsl.sql.ForeignKey;
import com.querydsl.sql.PrimaryKey;
import liquibase.structure.core.Column;
import liquibase.structure.core.DataType;
import liquibase.structure.core.Index;
import liquibase.structure.core.Table;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.util.Assert;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.ColumnMetaDataInfo;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.DynamicTableHelper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.IndexData;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public abstract class TableFactory {

    public static Table createTable(QDynamicTable dynamicTable) {
        Assert.notNull(dynamicTable);
        Table table = new Table("", "", dynamicTable.getTableName());
        table.setAttribute("columns", Lists.newArrayList());
        List<Path<?>> columns = dynamicTable.getColumns();
        for (Path column : columns) {
            table.getColumns().add(buildColumn(table, dynamicTable.getMetaInfo(column)));
        }
        PrimaryKey primaryKey = dynamicTable.getPrimaryKey();
        if (primaryKey != null) {
            table.setPrimaryKey(buildPrimaryKey(table, primaryKey));
        }
        Collection<ForeignKey<?>> foreignKeys = dynamicTable.getForeignKeys();
        table.setAttribute("outgoingForeignKeys", Lists.newArrayList());
        if (CollectionUtils.isNotEmpty(foreignKeys)) {
            int cnt = 1;
            for (ForeignKey<?> foreignKey : foreignKeys) {
                table.getOutgoingForeignKeys().add(buildForeignKey(table, foreignKey, cnt));
                cnt++;
            }
        }
        List<IndexData> indexDatas = DynamicTableHelper.getIndexDatas(dynamicTable);
        table.setAttribute("indexes", Lists.newArrayList());
        if (CollectionUtils.isNotEmpty(indexDatas)) {
            for (int i = 0; i < indexDatas.size(); i++) {

                table.getIndexes().add(buildIndex(table, indexDatas.get(i),
                        "IDX_" + table.getName() + i));
            }
        }
        return table;
    }

    private static Column buildColumn(Table table, ColumnMetaDataInfo columnMetaDataInfo) {
        Column column = new Column(ModelHelper
                .getColumnRealName(columnMetaDataInfo.getColumn()));
        column.setNullable(columnMetaDataInfo.isNullable());
        column.setRelation(table);
        column.setType(getDataType(columnMetaDataInfo));
        return column;

    }

    private static Index buildIndex(Table table, IndexData indexData, String name) {
        String tableName = table.getName();
        Column column = new Column(ModelHelper.getColumnRealName(indexData.getColumn()));
        column.setRelation(table);
        Index index = new Index(name, "", "", tableName, column);
        index.setUnique(indexData.isUnique());
        return index;
    }


    private static liquibase.structure.core.PrimaryKey buildPrimaryKey(
            Table table, PrimaryKey primaryKey) {
        liquibase.structure.core.PrimaryKey pk = new liquibase.structure.core.PrimaryKey();
        pk.setName(table.getName() + "PK_01");
        pk.setTable(table);
        Path primaryColumn = (Path) primaryKey.getLocalColumns().get(0);
        pk.addColumn(0, table.getColumn(ModelHelper.getColumnRealName(primaryColumn)));
        return pk;
    }

    private static liquibase.structure.core.ForeignKey buildForeignKey(
            Table table, ForeignKey foreignKey, int count) {
        Path fkColumn = (Path) foreignKey.getLocalColumns().get(0);
        liquibase.structure.core.ForeignKey fk = new liquibase
                .structure.core.ForeignKey(table.getName() + "FK" + count,
                "",
                "",
                table.getName(),
                table.getColumn(ModelHelper.getColumnRealName(fkColumn)));
        fk.setPrimaryKeyTable(new Table("", "", ModelHelper
                .getTableName(foreignKey.getEntity())));
        Path primaryKeyColumn = ModelHelper.getPrimaryKeyColumn(foreignKey.getEntity());
        fk.setPrimaryKeyColumns(Collections.singletonList(new Column(ModelHelper
                .getColumnRealName(primaryKeyColumn))));
        return fk;
    }

    private static DataType getDataType(ColumnMetaDataInfo columnMetadata) {
        DataType dataType = new DataType();
        dataType.setColumnSize(columnMetadata.getSize());
        dataType.setColumnSizeUnit(DataType.ColumnSizeUnit.BYTE);
        if (columnMetadata.getDecimalDigits() != null &&
                columnMetadata.getDecimalDigits() > -1) {
            dataType.setDecimalDigits(columnMetadata.getDecimalDigits());
        }
        dataType.setTypeName(columnMetadata.getJdbcType());
        return dataType;
    }
}
