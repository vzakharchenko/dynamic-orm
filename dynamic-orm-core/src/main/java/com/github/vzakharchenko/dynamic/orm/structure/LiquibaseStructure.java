package com.github.vzakharchenko.dynamic.orm.structure;

import com.github.vzakharchenko.dynamic.orm.structure.exception.DropAllException;
import com.github.vzakharchenko.dynamic.orm.structure.exception.UpdateException;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.List;

/**
 *
 */
public abstract class LiquibaseStructure extends AbstractLiquibaseStructure {
    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseStructure.class);

    @Override
    public String save(DataSource dataSource) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        LOGGER.info("Start Save file");
        try {
            Database database = currentDataBase(connection);
            DiffToChangeLog diffToChangeLog = getFullDiff(database);
            List<ChangeSet> changeSets = diffToChangeLog.generateChangeSets();
            updateChangeSets(changeSets);
            ByteArrayOutputStream outputStream = getOutputStream();
            new XMLChangeLogSerializer().write(changeSets, outputStream);
            outputStream.flush();
            return upload(database, outputStream);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void update(DataSource dataSource, String fileName) throws UpdateException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            LOGGER.info("Start update file: " + fileName);
            Liquibase liquibase = getLiquibase(connection);
            initDatabaseChangeLog(connection, liquibase.getDatabaseChangeLog(), fileName);
            liquibase.update(CONTEXT_NAME);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new UpdateException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void dropAll(DataSource dataSource) throws DropAllException {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            LOGGER.info("Start dropAll");
            getLiquibase(connection).dropAll();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DropAllException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public String getSqlScript(DataSource dataSource) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            LOGGER.info("generate Sql");
            StringWriter stringWriter = new StringWriter();
            Liquibase liquibase = getLiquibase(connection);
            initDatabaseChangeLog(connection, liquibase.getDatabaseChangeLog(), "*");
            liquibase.update(CONTEXT_NAME, stringWriter);
            return stringWriter.toString();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    @Override
    public void unlock(DataSource dataSource) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            LOGGER.info("unlock database");
            Liquibase liquibase = getLiquibase(connection);
            liquibase.forceReleaseLocks();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }
}
