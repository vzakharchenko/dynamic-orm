package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.github.vzakharchenko.dynamic.orm.core.OrmQueryFactory;
import liquibase.database.Database;
import org.apache.commons.lang3.StringUtils;

public class AbstractRemoveDynamicContext extends AbstractDynamicContext {


    public AbstractRemoveDynamicContext(Database database, OrmQueryFactory ormQueryFactory) {
        super(database, ormQueryFactory);
    }

    public QDynamicTable createQTable(String tableName) {
        updateDynamicTables();
        QDynamicTable qDynamicTable = dynamicTableMap.get(StringUtils.upperCase(tableName));
        return qDynamicTable != null ? qDynamicTable : new QDynamicTable(tableName);
    }


    public void removeQ(String name) {
        dynamicTableMap.remove(StringUtils.upperCase(name));
        viewMap.remove(StringUtils.upperCase(name));
    }

    public void removeSequence(String name) {
        sequenceModelMap.remove(StringUtils.upperCase(name));
    }
}
