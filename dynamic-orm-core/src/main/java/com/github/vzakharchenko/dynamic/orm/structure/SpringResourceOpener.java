package com.github.vzakharchenko.dynamic.orm.structure;

import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by vzakharchenko on 04.07.14.
 */
public class SpringResourceOpener extends ClassLoaderResourceAccessor {

    private static final ResourceLoader RESOURCE_LOADER = new PathMatchingResourcePatternResolver();

    // CHECKSTYLE:OFF
    @Override
    public SortedSet<String> list(String relativeTo, String path, boolean includeFiles,
                                  boolean includeDirectories, boolean recursive) throws IOException {
        SortedSet<String> returnSet = new TreeSet<>();

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
    public InputStream openStream(String relativeTo, String streamPath) throws IOException {
        Resource resource = RESOURCE_LOADER.getResource(streamPath);
        return resource.getInputStream();
    }
}
