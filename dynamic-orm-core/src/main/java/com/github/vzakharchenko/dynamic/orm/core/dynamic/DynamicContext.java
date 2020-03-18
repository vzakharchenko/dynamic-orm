package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaHelper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaLoader;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaLoaderHelper;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaSaver;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.models.Schema;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;

/**
 *
 */
public class DynamicContext extends AbstractRemoveDynamicContext {


    public DynamicContext(Database database, OrmQueryFactory queryFactory) {
        super(database, queryFactory);
    }

    public QDynamicTable getQTable0(String tableName) {
        updateDynamicTables();
        updateDynamicViews();
        QDynamicTable qDynamicTable = dynamicTableMap.get(StringUtils.upperCase(tableName));
        ViewDataHolder qViewTable = viewMap.get(StringUtils.upperCase(tableName));
        if (qDynamicTable == null && qViewTable == null) {
            return null;
        }
        return qDynamicTable != null ? qDynamicTable : qViewTable.getDynamicTable();
    }

    public QDynamicTable getQTable(String tableName) {
        QDynamicTable qTable = getQTable0(tableName);
        if (qTable == null) {
            throw new IllegalStateException("dynamic table with name " + tableName +
                    " is not found. May be you should Build this table first ");
        }
        return qTable;
    }

    public boolean isQTableExist(String tableName) {
        QDynamicTable qTable = getQTable0(tableName);
        return qTable != null;
    }

    public Database getDatabase(Connection connection) {
        database.setConnection(new JdbcConnection(connection));
        return database;
    }

    public void clear() {
        dynamicTableMap.clear();
    }

    public void saveSchema(SchemaSaver schemaSaver) {
        updateDynamicTables();
        updateDynamicViews();
        updateSequences();
        Schema schema = SchemaHelper.transform(dynamicTableMap, viewMap, sequenceModelMap);
        schemaSaver.save(schema);
    }

    public void loadSchema(QDynamicTableFactory dynamicTableFactory,
                           SchemaLoader schemaLoader) {
        updateDynamicTables();
        updateDynamicViews();
        updateSequences();
        Schema schema = schemaLoader.load();
        SchemaLoaderHelper.loadStructure(dynamicTableFactory, schema);
        dynamicTableFactory
                .buildSchema();
    }
}
