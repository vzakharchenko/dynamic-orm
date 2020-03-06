package com.github.vzakharchenko.dynamic.orm.core;

import com.querydsl.sql.SQLCommonQuery;

/**
 * All queries for fetch data and data models from a database
 */
public interface RawSelectBuilder {

    /**
     * Allows to query specific columns, as well as Aggregate Functions
     * <p>
     * example:
     * List<RawModel> rawModels = ormQueryFactory.select().rawSelect(ormQueryFactory.buildQuery()
     * .from(QTable.table).groupBy(QTable.table.name)).findAll(QTable.table.name,WildCard.count);
     * for(RawModel rawModel: rawModels){
     * String name = rawModel.getColumnValue(Table.table.name);
     * Long cnt =  rawModel.getColumnValue(WildCard.count);
     * }
     *
     * @param sqlQuery querydsl query
     * @return Builder
     * @see RawModelBuilder
     */
    RawModelBuilder rawSelect(SQLCommonQuery<?> sqlQuery);

}
