package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.ColumnMetaDataInfo;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.DynamicTableHelper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.IndexData;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.helper.ModelHelper;
import com.github.vzakharchenko.dynamic.orm.core.helper.PrimaryKeyHelper;
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

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class TableFactory {

    private static void createColumns(QDynamicTable dynamicTable,
                                      Table table
    ) {
        table.setAttribute("columns", Lists.newArrayList());
        dynamicTable.getColumns().forEach(column -> table.getColumns()
                .add(buildColumn(table, dynamicTable.getMetaInfo(column))));
        table.setAttribute("deletedObjects", dynamicTable.deletedColumns());
    }

    private static void createPrivateKey(QDynamicTable dynamicTable,
                                         Table table) {
        PrimaryKey primaryKey = dynamicTable.getPrimaryKey();
        if (primaryKey != null) {
            table.setPrimaryKey(buildPrimaryKey(table, primaryKey));
        }
    }

    private static void createForeignKeys(QDynamicTable dynamicTable,
                                          Table table) {
        Collection<ForeignKey<?>> foreignKeys = dynamicTable.getForeignKeys();
        table.setAttribute("outgoingForeignKeys", Lists.newArrayList());
        if (CollectionUtils.isNotEmpty(foreignKeys)) {
            int cnt = 1;
            for (ForeignKey<?> foreignKey : foreignKeys) {
                table.getOutgoingForeignKeys().add(buildForeignKey(table, foreignKey, cnt));
                cnt++;
            }
        }
    }

    private static void createIndexes(QDynamicTable dynamicTable,
                                      Table table) {
        List<IndexData> indexDatas = DynamicTableHelper.getIndexDatas(dynamicTable);
        table.setAttribute("indexes", Lists.newArrayList());
        if (CollectionUtils.isNotEmpty(indexDatas)) {
            for (int i = 0; i < indexDatas.size(); i++) {
                table.getIndexes().add(buildIndex(table, indexDatas.get(i),
                        "IDX_" + table.getName() + i));
            }
        }
    }

    public static Table createTable(QDynamicTable dynamicTable) {
        Assert.notNull(dynamicTable);
        Table table = new Table("", "", dynamicTable.getTableName());
        createColumns(dynamicTable, table);
        createPrivateKey(dynamicTable, table);
        createForeignKeys(dynamicTable, table);
        createIndexes(dynamicTable, table);
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
        Index index = new Index(name, "", "", tableName);
        index.setUnique(indexData.isUnique());
        index.setColumns(indexData.getColumns().stream().map(path -> {
            Column column = new Column(ModelHelper.getColumnRealName(path));
            column.setRelation(table);
            return column;
        }).collect(Collectors.toList()));
        index.setClustered(indexData.isClustered());
        return index;
    }


    private static liquibase.structure.core.PrimaryKey buildPrimaryKey(
            Table table, PrimaryKey primaryKey) {
        liquibase.structure.core.PrimaryKey pk = new liquibase.structure.core.PrimaryKey();
        pk.setName(table.getName() + "PK_01");
        pk.setTable(table);
        PrimaryKeyHelper.getPrimaryKeyColumns(primaryKey.getEntity())
                .forEach((Consumer<Path>) primaryColumn ->
                        pk.addColumn(pk.getColumns().size(),
                                table.getColumn(ModelHelper.getColumnRealName(primaryColumn))));
        return pk;
    }

    private static liquibase.structure.core.ForeignKey buildForeignKey(
            Table table, ForeignKey foreignKey, int count) {
        List<Path<?>> fkColumns = foreignKey.getLocalColumns();
        liquibase.structure.core.ForeignKey fk = new liquibase
                .structure.core.ForeignKey(table.getName() + "FK" + count,
                "",
                "",
                table.getName());
        fk.setForeignKeyColumns(fkColumns.stream().map(path ->
                new Column(ModelHelper.getColumnRealName(path))).collect(Collectors.toList()));
        fk.setPrimaryKeyTable(new Table("", "", ModelHelper
                .getTableName(foreignKey.getEntity())));
        List<? extends Path<?>> primaryKeyColumns = PrimaryKeyHelper
                .getPrimaryKeyColumns(foreignKey.getEntity());
        fk.setPrimaryKeyColumns(primaryKeyColumns.stream().map((Function<Path<?>, Column>)
                primaryKeyColumn -> new Column(ModelHelper
                        .getColumnRealName(primaryKeyColumn))).collect(Collectors.toList()));
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
