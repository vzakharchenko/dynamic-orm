package com.github.vzakharchenko.dynamic.orm.structure;

import com.github.vzakharchenko.dynamic.orm.structure.exception.UploadException;
import liquibase.database.Database;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.nio.file.Files;

/**
 *
 */
public class SimpleDbStructure extends LiquibaseStructure {


    private static final String DEFAULT_PREFIX = "unknown_";
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleDbStructure.class);
    private ResourceAccessor resourceAccessor = new SpringResourceOpener();
    private String pathToChangeSets0;
    private String pathToSaveChangeSets = ".";
    private String prefix;


    @Override
    protected String pathToChangeSets() {
        return pathToChangeSets0;
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
            try (OutputStream output = Files.newOutputStream(file.toPath())) {
                IOUtils.write(outputStream.toByteArray(), output);
            }
            LOGGER.info("upload " + fileName + " Success");
            return fileName;
        } catch (IOException e) {
            throw new UploadException(e);
        }
    }

    protected String generateFileName(Database database) {
        return getIdPrefix() + database.getDefaultCatalogName() + ".xml";
    }

    public final void setPathToChangeSets(String pathToChangeSets) {
        this.pathToChangeSets0 = pathToChangeSets;
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
