package com.github.vzakharchenko.dynamic.orm.core.helper;

import com.github.vzakharchenko.dynamic.orm.model.Testtable;
import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import com.querydsl.core.types.Path;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.assertNotNull;

public class TableUpdateHelperTest {
    @Test
    public void testTableUpdateHelper() {
        Testtable testtable = new Testtable();
        Map<Path<?>, Object> pathObjectMap = TableUpdateHelper.buildSetsValueForQModelAsMap(testtable, QTesttable.testtable,
                QTesttable.testtable.test2);
        assertNotNull(pathObjectMap);
    }
}
