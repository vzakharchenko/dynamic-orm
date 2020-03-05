package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CachedColumnTest {
    @Test
    public void testCachedColumn() {
        CachedColumn cachedColumn0 = new CachedColumn();
        assertNull(cachedColumn0.getColumnName());
        assertNull(cachedColumn0.getTableName());
        CachedColumn cachedColumn1 = new CachedColumn(QTesttable.testtable.id);
        CachedColumn cachedColumn2 = new CachedColumn("TESTTABLE", "ID");
        assertEquals(cachedColumn1, cachedColumn2);
        assertTrue(cachedColumn2.hashCode() > 0);


    }
}
