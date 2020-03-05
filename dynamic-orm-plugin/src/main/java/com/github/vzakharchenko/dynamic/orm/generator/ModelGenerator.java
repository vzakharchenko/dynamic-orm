package com.github.vzakharchenko.dynamic.orm.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Created by vzakharchenko on 05.11.14.
 */
@Mojo(name = "modelGenerator")
public class ModelGenerator extends AbstractMojo {
    private GenerateModelFactory generateModelFromQObjects = new GenerateModelFactory();

    @Parameter(property = "modelGenerator.targetQModelFolder")
    private String targetQModelFolder;

    @Parameter(property = "modelGenerator.qmodelPackage")
    private String qmodelPackage;

    @Parameter(property = "modelGenerator.modelPackage")
    private String modelPackage;

    @Parameter(property = "modelGenerator.mask")
    private String mask = "(Q)(.*)";


    protected AbstractGenerateModelFactory generateModelFactory() {
        return generateModelFromQObjects;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            generateModelFactory().generate(
                    targetQModelFolder, modelPackage, mask, qmodelPackage);
            getLog().info("Auto generate models was successfully");
        } catch (Exception ex) {
            getLog().error(ex);
            throw new MojoExecutionException(ex.getMessage(), ex);
        }
    }

    public void setGenerateModelFromQObjects(GenerateModelFactory generateModelFromQObjects) {
        this.generateModelFromQObjects = generateModelFromQObjects;
    }

    public void setTargetQModelFolder(String targetQModelFolder) {
        this.targetQModelFolder = targetQModelFolder;
    }

    public void setQmodelPackage(String qmodelPackage) {
        this.qmodelPackage = qmodelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
