package com.github.vzakharchenko.dynamic.orm.dataSource;

import org.hsqldb.jdbc.JDBCDriver;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 26.04.15
 * Time: 20:35
 */
public abstract class DataSourceHelper {

    public static DataSource getDataSourceHsqldbCreateSchema(String dbUrl) {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(JDBCDriver.class);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return new OrmDataSource(dataSource);
    }
}
