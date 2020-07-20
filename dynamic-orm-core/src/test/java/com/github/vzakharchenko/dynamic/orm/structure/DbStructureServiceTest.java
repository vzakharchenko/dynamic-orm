package com.github.vzakharchenko.dynamic.orm.structure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: vassio
 * Date: 13.04.15
 * Time: 10:53
 */
@ContextConfiguration(
        locations = "/test-spring-structure-context.xml")
public class DbStructureServiceTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private DbStructureService dbStructureService;

    @Test
    public void testLoad() {
        dbStructureService.load();
    }

    @Test
    public void testSave() {
        dbStructureService.save();
    }

    @Test
    public void testClear() {
        dbStructureService.clear();
        dbStructureService.load();
        dbStructureService.clear();
        dbStructureService.load();
    }

    @Test
    public void testSQl() {
        String sql = dbStructureService.generateSql();
        assertNotNull(sql);
    }

    @Test
    public void testSimpleDbStructure() {
        SimpleDbStructure simpleDbStructure = new SimpleDbStructure();
        simpleDbStructure.setResourceAccessor(null);
        simpleDbStructure.setPathToSaveChangeSets("target");
        simpleDbStructure.setFileNameComporator(mock(Comparator.class));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSimpleDbStructureNull() {
        SimpleDbStructure simpleDbStructure = new SimpleDbStructure();
        simpleDbStructure.setPathToSaveChangeSets(null);
    }
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSimpleDbStructureFail1() {
        SimpleDbStructure simpleDbStructure = new SimpleDbStructure();
        simpleDbStructure.setPathToSaveChangeSets("");
    }

    @Test
    public void testSpringResourceOpener() throws IOException {
        SpringResourceOpener springResourceOpener = new SpringResourceOpener();
        InputStream inputStream = springResourceOpener.openStream("","classpath:testSchema.json");
        assertNotNull(inputStream);

    }

}
