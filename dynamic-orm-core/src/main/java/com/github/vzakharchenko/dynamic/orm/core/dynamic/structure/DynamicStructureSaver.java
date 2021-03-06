package com.github.vzakharchenko.dynamic.orm.core.dynamic.structure;

import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.DynamicDatabaseSnapshot;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.structure.liquibase.DynamicDatabaseSnapshotFactory;
import com.github.vzakharchenko.dynamic.orm.structure.SimpleDbStructure;
import com.github.vzakharchenko.dynamic.orm.structure.exception.UpdateException;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.DatabaseException;
import liquibase.serializer.core.xml.XMLChangeLogSerializer;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.InvalidExampleException;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;
import liquibase.structure.DatabaseObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 *
 */
public class DynamicStructureSaver extends SimpleDbStructure implements DynamicStructureUpdater {

    public static final String TEMP_DIR_PROPERTY = "java.io.tmpdir";
    public static final String DYNAMIC = "dynamic";
    public static final String WORK = "work";
    public static final String SUCCESS = "success";
    public static final File TEMP_DIR = new File(System.getProperty(TEMP_DIR_PROPERTY), DYNAMIC);
    public static final File WORK_DIR = new File(TEMP_DIR, WORK);
    public static final File SUCCESS_DIR = new File(TEMP_DIR, SUCCESS);
    public static final String PATTERN = "yyyyMMdd_HHmm_SSS";
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DynamicStructureSaver.class);
    private final DataSource dataSource;

    public DynamicStructureSaver(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
        try {
            createTempDir();
            setPrefix(DYNAMIC);
            setPathToChangeSets("file:" + WORK_DIR.getAbsolutePath());

        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void checkChangeSet(List<ChangeSet> changeSets)
            throws IOException,
            UpdateException {
        String fileName = DYNAMIC + new SimpleDateFormat(PATTERN, Locale.getDefault()).format(
                new Date()) + "_" + System.nanoTime() + ".xml";
        File file = new File(WORK_DIR, FilenameUtils.getName(fileName));
        try (PrintStream out = new PrintStream(file, Charset.defaultCharset().name())) {
            XMLChangeLogSerializer xmlChangeLogSerializer = new XMLChangeLogSerializer();
            xmlChangeLogSerializer.write(changeSets, out);
        }
        LOGGER.error(FileUtils.readFileToString(file, Charset.defaultCharset()));
        update(dataSource, fileName);
        moveToTempDir(file);
    }

    private void createDiff(
            DatabaseSnapshot databaseSnapshot,
            DatabaseSnapshot referenceSnapshot
    ) throws DatabaseException, IOException,
            UpdateException {
        DiffResult diffResult = DiffGeneratorFactory.getInstance()
                .compare(databaseSnapshot, referenceSnapshot, getCompareControl());
        DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diffResult,
                getDiffOutputControl());
        diffToChangeLog.setChangeSetAuthor(AUTHOR_NAME);
        diffToChangeLog.setChangeSetContext(CONTEXT_NAME);
        diffToChangeLog.setIdRoot(getIdPrefix());
        List<ChangeSet> changeSets = diffToChangeLog.generateChangeSets();
        updateChangeSets(changeSets);
        if (CollectionUtils.isNotEmpty(changeSets)) {
            checkChangeSet(changeSets);
        }
    }

    private void update0(Connection connection,
                         LiquibaseHolder liquibaseHolder) throws IOException,
            DatabaseException,
            InvalidExampleException,
            UpdateException {
        clearTempDir();
        Database referenceDatabase = currentDataBase(connection);
        // generate snapshot database for definition
        DynamicDatabaseSnapshot databaseSnapshot = DynamicDatabaseSnapshotFactory
                .build(referenceDatabase, liquibaseHolder);
        // create snapshot for current database
        DatabaseSnapshot referenceSnapshot = getDatabaseSnapshot(referenceDatabase);
        // merge definition base and reference database(origin is definition)
        databaseSnapshot.mergedDatabase(referenceSnapshot);
        // generate DiFF
        createDiff(databaseSnapshot, referenceSnapshot);
    }

    @Override
    public void update(LiquibaseHolder liquibaseHolder) {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            update0(connection, liquibaseHolder);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }


    public DatabaseSnapshot getDatabaseSnapshot(Database referenceDatabase)
            throws DatabaseException, InvalidExampleException {
        Set<Class<? extends DatabaseObject>> compareTypes = getCompareTypes();
        return SnapshotGeneratorFactory.getInstance()
                .createSnapshot(
                        referenceDatabase.getDefaultSchema(), referenceDatabase,
                        new SnapshotControl(referenceDatabase,
                                compareTypes.toArray(new Class[compareTypes.size()])));
    }

    @Override
    public DatabaseSnapshot getDatabaseSnapshot() {
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            Database referenceDatabase = currentDataBase(connection);
            // create snapshot for current database
            return getDatabaseSnapshot(referenceDatabase);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }


    protected void createTempDir() throws IOException {
        FileUtils.forceMkdir(TEMP_DIR);
        FileUtils.forceMkdir(WORK_DIR);
        FileUtils.forceMkdir(SUCCESS_DIR);
    }

    protected void clearTempDir() throws IOException {
        FileUtils.cleanDirectory(WORK_DIR);
    }

    protected void moveToTempDir(File file) throws IOException {
        FileUtils.moveFileToDirectory(file, SUCCESS_DIR, false);
    }

}
