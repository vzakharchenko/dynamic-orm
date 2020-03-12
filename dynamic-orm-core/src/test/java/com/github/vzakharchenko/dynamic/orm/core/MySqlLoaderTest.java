package com.github.vzakharchenko.dynamic.orm.core;

import com.github.vzakharchenko.dynamic.orm.MYSQLTestQueryOrm;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.QDynamicTable;
import com.github.vzakharchenko.dynamic.orm.core.dynamic.schema.SchemaUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertNotNull;

public class MySqlLoaderTest extends MYSQLTestQueryOrm {

    private ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();

    @Test
    public void testLoad() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:testSchema.json");
        qDynamicTableFactory.loadSchema(SchemaUtils.getFileLoader(resource.getFile()));
        QDynamicTable table = qDynamicTableFactory.getQDynamicTableByName("TABLE");
        assertNotNull(table);
    }
}
