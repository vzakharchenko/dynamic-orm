package com.github.vzakharchenko.dynamic.orm.generator;

import com.github.vzakharchenko.dynamic.orm.structure.SimpleDbStructure;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;

/**
 * Created by vzakharchenko on 05.11.14.
 */
public class StaticDbStructureCreator extends SimpleDbStructure {

    private final DataSource dataSource;
    private String fileName;

    public StaticDbStructureCreator(
            Class driver, String dbUrl, String user, String password, String pathToChangeSets) {
        this.dataSource = getDataSource(driver, dbUrl, user, password);
        setPathToSaveChangeSets(pathToChangeSets);
    }

    private static DataSource getDataSource(
            Class driver, String dbUrl, String user, String password) {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(driver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Override
    protected void initDatabaseChangeLog(
            Connection connection,
            DatabaseChangeLog databaseChangeLog, String fileName0) {

    }


    @Override
    protected String generateFileName(Database database) {
        if (StringUtils.isEmpty(fileName)) {
            return super.generateFileName(database);
        } else {
            return fileName;
        }
    }

    public synchronized void createChangesets(File file, String changeSetFileName) {
        fileName = changeSetFileName;
        setPathToSaveChangeSets(file.getAbsolutePath());
        save(dataSource);
    }
}
