package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import javax.sql.DataSource;
import java.util.Map;

public interface QDynamicContextHolder {
    DataSource getDataSource();

    QDynamicTable getDynamicTable();

    DynamicContext getDynamicContext();

    Map<String, QDynamicTable> getContextTables();


}
