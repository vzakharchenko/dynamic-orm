package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import javax.sql.DataSource;
import java.util.Map;

public interface QDynamicContextHolder {
    DataSource getDataSource();

    DynamicContext getDynamicContext();

    Map<String, QDynamicTable> getContextTables();

    Map<String, SequanceModel> getContextSequances();

    Map<String, ViewModel> getViewSequances();
}
