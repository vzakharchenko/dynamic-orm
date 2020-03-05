package com.github.vzakharchenko.dynamic.orm.structure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

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

}
