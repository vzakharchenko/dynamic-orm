package com.github.vzakharchenko.dynamic.orm.structure;

import org.hsqldb.jdbc.JDBCDriver;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 8:54
 */
public class DataSourceHelper {


    public static DataSource getDataSourceHsqldbCreateSchema(String dbUrl) throws Exception {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(JDBCDriver.class);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }
}
