package com.github.vzakharchenko.dynamic.orm.core.dynamic;

import com.querydsl.core.types.Expression;
import com.querydsl.sql.SQLCommonQuery;


public interface QViewBuilder {

    QViewBuilder resultSet(SQLCommonQuery<?> query, Expression<?>... column);

    QDynamicTableFactory finish();
}
