package com.github.vzakharchenko.dynamic.orm.structure;

import liquibase.resource.ResourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vzakharchenko on 04.07.14.
 */
public class SpringResourceOpener implements ResourceAccessor {

    private static final ResourceLoader RESOURCE_LOADER = new PathMatchingResourcePatternResolver();
    // CHECKSTYLE:OFF
    @Override
    public Set<String> list(String relativeTo, String path, boolean includeFiles,
                            boolean includeDirectories, boolean recursive) throws IOException {
        Set<String> returnSet = new HashSet<>();

        Resource[] resources = ResourcePatternUtils
                .getResourcePatternResolver(RESOURCE_LOADER).getResources(adjustClasspath(path));

        for (Resource res : resources) {

            if (res instanceof ClassPathResource) {
                returnSet.add(((ClassPathResource) res).getPath());
            } else {
                returnSet.add(res.getURL().toExternalForm());
            }
        }

        return returnSet;
    }
    // CHECKSTYLE:ON
    @Override
    public Set<InputStream> getResourcesAsStream(String path) throws IOException {

        Resource[] resources = ResourcePatternUtils
                .getResourcePatternResolver(RESOURCE_LOADER).getResources(adjustClasspath(path));

        if (resources.length == 0) {
            return null;
        }
        Set<InputStream> returnSet = new HashSet<>(resources.length);
        for (Resource resource : resources) {
            returnSet.add(resource.getInputStream());
        }

        return returnSet;
    }

    public Resource getResource(String file) {
        return RESOURCE_LOADER.getResource(adjustClasspath(file));
    }

    private String adjustClasspath(String file) {
        return !isPrefixPresent(file) ? ResourceLoader.CLASSPATH_URL_PREFIX + file : file;
    }

    public boolean isPrefixPresent(String file) {
        // file:
        // vfs:
        // classpath:
        return file.matches("(\\w{2,})(:)(.*)");
    }

    @Override
    public ClassLoader toClassLoader() {
        return RESOURCE_LOADER.getClassLoader();
    }
}
