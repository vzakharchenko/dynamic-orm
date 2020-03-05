package com.github.vzakharchenko.dynamic.orm.generator;

import com.querydsl.sql.RelationalPath;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractGenerateModelFactory {

    protected final String message = "/*\n/*   autoGenerate by " + getClass() + "\n*/";
    protected final ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();


    public abstract void initialization(
            String modelPackage, String modelsPath, List<Class<?>> classList);

    public void generate(
            String modelsPath, String modelPackage, String mask, String qModelPackage)
            throws Exception {
        List<Class<?>> classList = getClassesForPackage(
                qModelPackage, StringUtils.defaultString(mask, "(.*)"));
        initialization(modelPackage, modelsPath, classList);
        for (Class aClass : classList) {
            generate(aClass, generateName(aClass), modelPackage, modelsPath);
        }
        finalization(modelPackage, modelsPath);
    }

    public abstract void finalization(String modelPackage, String modelsPath);

    protected String generateName(Class aclass) {
        String className = aclass.getSimpleName();
        String name = StringUtils.removeStartIgnoreCase(className, "q");
        return WordUtils.capitalize(name);
    }


    public abstract void generate(
            Class<? extends RelationalPath<?>> aclass, String name,
            String modelPackage, String modelsPath) throws Exception;

    private List<Class<?>> getClassesForPackage(String pkgname, String fileNameMask) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        // Get a File object for the package
        try {
            Resource[] resources = ResourcePatternUtils
                    .getResourcePatternResolver(resourceLoader)
                    .getResources(ResourceLoader.CLASSPATH_URL_PREFIX +
                            StringUtils
                                    .replaceChars(pkgname,
                                            ".",
                                            "/") + "/*");
            for (Resource resource : resources) {
                String className = FilenameUtils.getBaseName(resource.getFilename());
                if (className.matches(fileNameMask)) {
                    classes.add(resource.getClass().getClassLoader().loadClass(
                            pkgname + "." + className));
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return classes;
    }
}
