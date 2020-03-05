package com.github.vzakharchenko.dynamic.orm.core.cache;

import com.github.vzakharchenko.dynamic.orm.qModel.QTesttable;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class CachedAllDataTest {


    @Test
    public void testCachedAllData0() {
        CachedAllData cachedAllData = new CachedAllData();
        assertNull(cachedAllData.getTableName());
    }

    @Test
    public void testCachedAllData1() {
        CachedAllData cachedAllData = new CachedAllData(QTesttable.testtable);
        assertEquals(cachedAllData.getTableName(), "TESTTABLE");

    }

    @Test
    public void testCachedAllData2() {
        CachedAllData cachedAllData = new CachedAllData("TESTTABLE");
        assertEquals(cachedAllData.getTableName(), "TESTTABLE");
        assertTrue(cachedAllData.hashCode() != 0);

    }

    @Test
    public void testCachedAllDataEquals() {
        CachedAllData cachedAllData1 = new CachedAllData("TESTTABLE");
        CachedAllData cachedAllData2 = new CachedAllData(QTesttable.testtable);
        assertEquals(cachedAllData1, cachedAllData2);

    }
}
