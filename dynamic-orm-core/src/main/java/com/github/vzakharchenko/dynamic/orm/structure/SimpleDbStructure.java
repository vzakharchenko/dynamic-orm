package com.github.vzakharchenko.dynamic.orm.structure;

import com.github.vzakharchenko.dynamic.orm.structure.exception.UploadException;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class SimpleDbStructure extends LiquibaseStructure {


    private static final String DEFAULT_PREFIX = "unknown_";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbStructure.class);
    private ResourceAccessor resourceAccessor = new SpringResourceOpener();
    private String pathToChangeSets;
    private String pathToSaveChangeSets = ".";
    private String prefix;


    @Override
    protected String pathToChangeSets() {
        return pathToChangeSets;
    }

    @Override
    protected String getIdPrefix() {
        return StringUtils.isNotEmpty(prefix) ? prefix : DEFAULT_PREFIX;
    }

    @Override
    protected ResourceAccessor getResourceAccessor() {
        return resourceAccessor;
    }

    public void setResourceAccessor(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    @Override
    public String upload(Database currentDatabase, ByteArrayOutputStream outputStream)
            throws UploadException {
        try {
            String fileName = generateFileName(currentDatabase);
            File file = new File(pathToSaveChangeSets, fileName);
            IOUtils.write(outputStream.toByteArray(), new FileOutputStream(file));
            LOGGER.info("upload " + fileName + " Success");
            return fileName;
        } catch (IOException e) {
            throw new UploadException(e);
        }
    }

    protected String generateFileName(Database database) {
        return getIdPrefix() + database.getDefaultCatalogName() + ".xml";
    }

    public void setPathToChangeSets(String pathToChangeSets) {
        this.pathToChangeSets = pathToChangeSets;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPathToSaveChangeSets(String pathToSaveChangeSets) {
        Assert.notNull(pathToSaveChangeSets);
        File file = new File(pathToSaveChangeSets);
        if (!file.exists()) {
            Assert.isTrue(file.mkdirs());
        }
        Assert.isTrue(file.isDirectory(), pathToSaveChangeSets + " is not directory");
        if (!file.exists()) {
            Assert.isTrue(file.mkdirs());
        }
        this.pathToSaveChangeSets = pathToSaveChangeSets;
    }
}
