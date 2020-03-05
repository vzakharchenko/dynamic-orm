package com.github.vzakharchenko.dynamic.orm.generator;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

/**
 * Created by vzakharchenko on 05.11.14.
 */
@Mojo(name = "generateChangeSet")
public class ChangeSetGenerator extends AbstractMojo {
    @Parameter(property = "generateChangeSet.dbLogin")
    private String dbLogin;
    @Parameter(property = "generateChangeSet.dbPassword")
    private String dbPassword;
    @Parameter(property = "generateChangeSet.dbURL")
    private String dbURL;
    @Parameter(property = "generateChangeSet.dbDriver")
    private String dbDriver;
    @Parameter(property = "generateChangeSet.pathToChangeSets")
    private String pathToChangeSets;
    @Parameter(property = "generateChangeSet.changeSetFileName")
    private String changeSetFileName;

    @Override
    public void execute() {
        try {
            getLog().info("create " + changeSetFileName);
            FileUtils.forceMkdir(new File(changeSetFileName));
            StaticDbStructureCreator staticDbStructureCreator = new StaticDbStructureCreator(
                    Class.forName(dbDriver), dbURL, dbLogin, dbPassword, pathToChangeSets);
            staticDbStructureCreator.setCompareData(true);
            staticDbStructureCreator.createChangesets(new File(pathToChangeSets),
                    changeSetFileName);
            getLog().info(changeSetFileName + " created");

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            getLog().error(e);
        }

    }
}
