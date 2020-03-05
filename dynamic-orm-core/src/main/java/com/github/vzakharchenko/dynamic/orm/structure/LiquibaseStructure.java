package com.github.vzakharchenko.dynamic.orm.structure;

import com.github.vzakharchenko.dynamic.orm.structure.exception.DBException;
import com.github.vzakharchenko.dynamic.orm.structure.exception.DropAllException;
import com.github.vzakharchenko.dynamic.orm.structure.exception.UpdateException;
import com.github.vzakharchenko.dynamic.orm.structure.exception.UploadException;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.DatabaseException;
import liquibase.resource.ResourceAccessor;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.structure.DatabaseObject;
import liquibase.structure.core.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.*;

/**
 *
 */
public abstract class LiquibaseStructure implements DBStructure {
    protected static final String CONTEXT_NAME = "production";
    protected static final String AUTHOR_NAME = System.getProperty("user.name") + "(generate)";
    private static final Logger LOGGER = LoggerFactory.getLogger(LiquibaseStructure.class);
    private Comparator<String> fileNameComporator = StringComparator.getStringComparator();
    private boolean compareData = true;

    public LiquibaseStructure() {
    }

    protected Database currentDataBase(Connection connection) throws DatabaseException {
        return DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));
    }

    protected Liquibase getLiquibase(Connection connection) throws DatabaseException {
        return new Liquibase(getDatabaseChangeLog(),
                getResourceAccessor(), currentDataBase(connection));
    }

    protected DatabaseChangeLogReader getDatabaseChangeLogReader(Connection connection)
            throws DatabaseException {
        return new DatabaseChangeLogReaderImpl(getResourceAccessor(), currentDataBase(connection));
    }


    protected void initDatabaseChangeLog(Connection connection, DatabaseChangeLog databaseChangeLog,
                                         String fileName) throws DBException {
        try {
            LOGGER.info("read " + fileName);
            databaseChangeLog.getChangeSets().clear();
            databaseChangeLog.getPreconditions().getNestedPreconditions().clear();
            String path = pathToChangeSets() + (StringUtils
                    .endsWith(pathToChangeSets(), File.separator) ? "" : File.separator) + fileName;
            getDatabaseChangeLogReader(connection)
                    .read(databaseChangeLog, path, fileNameComporator);
            updateChangeSets(databaseChangeLog.getChangeSets());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DBException(e);
        }
    }


    protected DatabaseChangeLog getDatabaseChangeLog() {
        return new DatabaseChangeLog();
    }

    protected abstract String pathToChangeSets();

    protected Set<Class<? extends DatabaseObject>> getCompareTypes() {
        Set<Class<? extends DatabaseObject>> compareTypes = new HashSet<>();
        compareTypes.add(Table.class);
        compareTypes.add(PrimaryKey.class);
        compareTypes.add(ForeignKey.class);
        compareTypes.add(Column.class);
        compareTypes.add(Sequence.class);
        compareTypes.add(StoredProcedure.class);
        compareTypes.add(UniqueConstraint.class);
        compareTypes.add(Index.class);
        if (compareData) {
            compareTypes.add(Data.class);
        }
        return compareTypes;
    }

    protected CompareControl getCompareControl() {
        return new CompareControl(getCompareTypes());
    }


    protected DiffOutputControl getDiffOutputControl() {
        return new DiffOutputControl(false,
                false,
                false,
                null);
    }

    protected DiffToChangeLog getFullDiff(Database database) {
        LOGGER.info("get Full diff");
        try {
            DatabaseSnapshot referenceSnapshot = SnapshotGeneratorFactory.getInstance()
                    .createSnapshot(database
                            .getDefaultSchema(), database, new SnapshotControl(database,
                            getCompareTypes().toArray(new Class[getCompareTypes().size()])));
            DiffResult diffResult = DiffGeneratorFactory.getInstance()
                    .compare(referenceSnapshot, null, getCompareControl());
            DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diffResult,
                    getDiffOutputControl());
            diffToChangeLog.setChangeSetAuthor(AUTHOR_NAME);
            diffToChangeLog.setChangeSetContext(CONTEXT_NAME);
            diffToChangeLog.setIdRoot(getIdPrefix());
            return diffToChangeLog;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void updateChangeSets(Collection<ChangeSet> changeSets) {
        if (CollectionUtils.isNotEmpty(changeSets)) {
            changeSets.forEach(this::updateChangeSet);
        }
    }

    protected void updateChangeSet(ChangeSet changeSet) {
        changeSet.setFilePath(StringUtils.substringAfterLast(changeSet.getFilePath(),
                "/"));
    }

    protected abstract String getIdPrefix();


    protected abstract ResourceAccessor getResourceAccessor();

    protected ByteArrayOutputStream getOutputStream() {
        return new ByteArrayOutputStream();
    }

    public abstract String upload(Database currentDatabase,
                                  ByteArrayOutputStream outputStream) throws UploadException;

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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    public void setFileNameComporator(Comparator<String> fileNameComporator) {
        this.fileNameComporator = fileNameComporator;
    }

    public void setCompareData(boolean compareData) {
        this.compareData = compareData;
    }

    public static class StringComparator implements Comparator<String>, Serializable {

        private static final StringComparator COMPARATOR = new StringComparator();

        StringComparator() {
        }

        static StringComparator getStringComparator() {
            return COMPARATOR;
        }

        @Override
        public int compare(String o1, String o2) {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }
            String stemp1 = StringUtils.lowerCase(o1, Locale.getDefault());
            String stemp2 = StringUtils.lowerCase(o2, Locale.getDefault());

            return stemp1.compareTo(stemp2);
        }
    }
}
